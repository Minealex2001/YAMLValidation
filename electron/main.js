const { app, BrowserWindow, dialog, ipcMain } = require('electron');
const path = require('path');
const fs = require('fs').promises;
const { spawn } = require('child_process');
const Store = require('electron-store');

const store = new Store();

let mainWindow;

function createWindow() {
  const preloadPath = path.join(__dirname, 'preload.js');
  console.log('Preload path:', preloadPath);
  
  mainWindow = new BrowserWindow({
    width: store.get('window.width', 1600),
    height: store.get('window.height', 1200),
    minWidth: 1400,
    minHeight: 900,
    frame: false, // Sin barra de título estándar
    titleBarStyle: 'hidden', // Ocultar barra de título
    webPreferences: {
      preload: preloadPath,
      nodeIntegration: false,
      contextIsolation: true,
      sandbox: false
    },
    title: 'Validador YAML',
    backgroundColor: '#1C1B1F' // Color de fondo por defecto (Material 3 Dark)
  });

  const isDev = process.argv.includes('--dev');
  const indexPath = path.join(__dirname, '..', 'src', 'index.html');
  mainWindow.loadFile(indexPath);
  if (isDev) {
    mainWindow.webContents.openDevTools();
  }

  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  mainWindow.on('resize', () => {
    if (mainWindow) {
      const [width, height] = mainWindow.getSize();
      store.set('window.width', width);
      store.set('window.height', height);
    }
  });

  mainWindow.on('move', () => {
    if (mainWindow) {
      const [x, y] = mainWindow.getPosition();
      store.set('window.x', x);
      store.set('window.y', y);
    }
  });

  // Emitir eventos cuando la ventana se maximiza/restaura
  mainWindow.on('maximize', () => {
    mainWindow.webContents.send('window-maximize');
  });

  mainWindow.on('unmaximize', () => {
    mainWindow.webContents.send('window-unmaximize');
  });
}

app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// IPC Handlers
ipcMain.handle('dialog:showOpenFile', async (event, options) => {
  const result = await dialog.showOpenDialog(mainWindow, {
    title: options.title || 'Seleccionar archivo',
    filters: options.filters || [{ name: 'YAML', extensions: ['yaml', 'yml'] }],
    properties: ['openFile']
  });
  return result;
});

ipcMain.handle('dialog:showSaveFile', async (event, options) => {
  const result = await dialog.showSaveDialog(mainWindow, {
    title: options.title || 'Guardar archivo',
    defaultPath: options.defaultPath || 'salida_validacion.txt',
    filters: options.filters || [{ name: 'Text', extensions: ['txt'] }]
  });
  return result;
});

ipcMain.handle('dialog:showOpenDirectory', async (event, options) => {
  const result = await dialog.showOpenDialog(mainWindow, {
    title: options.title || 'Seleccionar carpeta',
    properties: ['openDirectory']
  });
  return result;
});

ipcMain.handle('fs:readFile', async (event, filePath) => {
  try {
    const content = await fs.readFile(filePath, 'utf-8');
    return { success: true, content };
  } catch (error) {
    return { success: false, error: error.message };
  }
});

ipcMain.handle('fs:writeFile', async (event, filePath, content) => {
  try {
    await fs.writeFile(filePath, content, 'utf-8');
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
});

ipcMain.handle('fs:exists', async (event, filePath) => {
  try {
    await fs.access(filePath);
    return { success: true, exists: true };
  } catch {
    return { success: true, exists: false };
  }
});

ipcMain.handle('fs:copyFile', async (event, source, dest) => {
  try {
    await fs.copyFile(source, dest);
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
});

ipcMain.handle('spectral:run', async (event, options) => {
  return new Promise((resolve) => {
    const { spectralPath, yamlPath, spectralDir } = options;
    
    try {
      const isWindows = process.platform === 'win32';
      let command;
      let args;
      
      if (isWindows) {
        // Windows: usar cmd /c para ejecutar spectral
        command = 'cmd';
        // Escapar correctamente las rutas con espacios en Windows
        const escapedYamlPath = yamlPath.replace(/"/g, '""');
        args = ['/c', `spectral lint -r ./poc/.spectral_v2.yaml -f pretty "${escapedYamlPath}"`];
      } else {
        // Linux/Mac: ejecutar spectral directamente
        command = 'spectral';
        args = ['lint', '-r', './poc/.spectral_v2.yaml', '-f', 'pretty', yamlPath];
      }
      
      // Determinar el directorio de trabajo
      // Si spectralPath es un directorio, usarlo; si no, usar el directorio del archivo YAML o process.cwd()
      let workingDir = spectralDir;
      if (!workingDir && spectralPath) {
        const fs = require('fs');
        try {
          const spectralPathStat = fs.statSync(spectralPath);
          if (spectralPathStat.isDirectory()) {
            workingDir = spectralPath;
          } else {
            workingDir = path.dirname(spectralPath);
          }
        } catch (e) {
          // Si no existe, asumir que es un directorio
          workingDir = spectralPath;
        }
      }
      if (!workingDir) {
        workingDir = process.cwd();
      }
      
      const processOptions = {
        cwd: workingDir,
        shell: isWindows, // En Windows, usar shell para que cmd funcione correctamente
        env: { ...process.env }
      };
      
      console.log('Ejecutando Spectral:', command, args);
      console.log('Directorio de trabajo:', workingDir);
      
      const spectralProcess = spawn(command, args, processOptions);
      
      let output = '';
      let errorOutput = '';
      
      spectralProcess.stdout.on('data', (data) => {
        output += data.toString();
      });
      
      spectralProcess.stderr.on('data', (data) => {
        errorOutput += data.toString();
      });
      
      spectralProcess.on('close', (code) => {
        // En Windows, spectral puede devolver código 0 incluso con warnings
        // Combinar stdout y stderr para obtener toda la salida
        const fullOutput = (output || '') + (errorOutput || '');
        resolve({
          success: fullOutput.trim().length > 0 || code === 0,
          output: fullOutput || errorOutput || output,
          error: code !== 0 && !fullOutput ? `Process exited with code ${code}` : null
        });
      });
      
      spectralProcess.on('error', (error) => {
        console.error('Error ejecutando Spectral:', error);
        resolve({
          success: false,
          output: '',
          error: error.message || 'Error ejecutando Spectral. Asegúrate de que Spectral esté instalado y en el PATH.'
        });
      });
    } catch (error) {
      console.error('Error en spectral:run:', error);
      resolve({
        success: false,
        output: '',
        error: error.message || 'Error desconocido ejecutando Spectral'
      });
    }
  });
});

ipcMain.handle('store:get', (event, key) => {
  return store.get(key);
});

ipcMain.handle('store:set', (event, key, value) => {
  store.set(key, value);
  return true;
});

ipcMain.handle('store:delete', (event, key) => {
  store.delete(key);
  return true;
});

// IPC Handlers para controles de ventana
ipcMain.handle('window:minimize', () => {
  if (mainWindow) {
    mainWindow.minimize();
  }
});

ipcMain.handle('window:maximize', () => {
  if (mainWindow) {
    if (mainWindow.isMaximized()) {
      mainWindow.unmaximize();
    } else {
      mainWindow.maximize();
    }
  }
});

ipcMain.handle('window:close', () => {
  if (mainWindow) {
    mainWindow.close();
  }
});

ipcMain.handle('window:isMaximized', () => {
  if (mainWindow) {
    return mainWindow.isMaximized();
  }
  return false;
});

// Git pull handler
ipcMain.handle('git:pull', async (event, directoryPath) => {
  return new Promise((resolve) => {
    try {
      const isWindows = process.platform === 'win32';
      let command;
      let args;
      
      if (isWindows) {
        command = 'cmd';
        args = ['/c', 'git', 'pull'];
      } else {
        command = 'git';
        args = ['pull'];
      }
      
      const processOptions = {
        cwd: directoryPath,
        shell: isWindows,
        env: { ...process.env }
      };
      
      console.log('Ejecutando git pull en:', directoryPath);
      
      const gitProcess = spawn(command, args, processOptions);
      
      let output = '';
      let errorOutput = '';
      
      gitProcess.stdout.on('data', (data) => {
        output += data.toString();
      });
      
      gitProcess.stderr.on('data', (data) => {
        errorOutput += data.toString();
      });
      
      gitProcess.on('close', (code) => {
        const fullOutput = (output || '') + (errorOutput || '');
        resolve({
          success: code === 0,
          output: fullOutput || output || errorOutput,
          error: code !== 0 ? `Git pull exited with code ${code}` : null
        });
      });
      
      gitProcess.on('error', (error) => {
        console.error('Error ejecutando git pull:', error);
        resolve({
          success: false,
          output: '',
          error: error.message || 'Error ejecutando git pull. Asegúrate de que git esté instalado y en el PATH.'
        });
      });
    } catch (error) {
      console.error('Error en git:pull:', error);
      resolve({
        success: false,
        output: '',
        error: error.message || 'Error desconocido ejecutando git pull'
      });
    }
  });
});

