const { contextBridge, ipcRenderer } = require('electron');
const path = require('path');

let yaml;
try {
  // Resolver la ruta del módulo js-yaml
  const projectRoot = path.resolve(__dirname, '..');
  process.chdir(projectRoot); // Cambiar al directorio raíz del proyecto
  
  // Intentar cargar el módulo
  yaml = require('js-yaml');
  console.log('js-yaml loaded successfully');
} catch (error) {
  console.error('Error loading js-yaml:', error);
  console.error('Current directory:', process.cwd());
  console.error('__dirname:', __dirname);
  
  // Intentar cargar desde la ruta absoluta
  try {
    const yamlPath = path.resolve(__dirname, '..', 'node_modules', 'js-yaml');
    yaml = require(yamlPath);
    console.log('js-yaml loaded from absolute path');
  } catch (error2) {
    console.error('Failed to load js-yaml from absolute path:', error2);
    yaml = null;
  }
}

contextBridge.exposeInMainWorld('electronAPI', {
  // Dialog APIs
  showOpenFile: (options) => ipcRenderer.invoke('dialog:showOpenFile', options),
  showSaveFile: (options) => ipcRenderer.invoke('dialog:showSaveFile', options),
  showOpenDirectory: (options) => ipcRenderer.invoke('dialog:showOpenDirectory', options),
  
  // File System APIs
  readFile: (filePath) => ipcRenderer.invoke('fs:readFile', filePath),
  writeFile: (filePath, content) => ipcRenderer.invoke('fs:writeFile', filePath, content),
  exists: (filePath) => ipcRenderer.invoke('fs:exists', filePath),
  copyFile: (source, dest) => ipcRenderer.invoke('fs:copyFile', source, dest),
  
  // Spectral API
  runSpectral: (options) => ipcRenderer.invoke('spectral:run', options),
  
  // Store APIs
  storeGet: (key) => ipcRenderer.invoke('store:get', key),
  storeSet: (key, value) => ipcRenderer.invoke('store:set', key, value),
  storeDelete: (key) => ipcRenderer.invoke('store:delete', key),
  
  // Window Controls APIs
  windowMinimize: () => ipcRenderer.invoke('window:minimize'),
  windowMaximize: () => ipcRenderer.invoke('window:maximize'),
  windowClose: () => ipcRenderer.invoke('window:close'),
  windowIsMaximized: () => ipcRenderer.invoke('window:isMaximized'),
  
  // Window events
  onWindowMaximize: (callback) => {
    ipcRenderer.on('window-maximize', callback);
    return () => ipcRenderer.removeListener('window-maximize', callback);
  },
  onWindowUnmaximize: (callback) => {
    ipcRenderer.on('window-unmaximize', callback);
    return () => ipcRenderer.removeListener('window-unmaximize', callback);
  },
  
  // Git API
  gitPull: (directoryPath) => ipcRenderer.invoke('git:pull', directoryPath)
});

// Expose js-yaml to renderer
if (yaml) {
  contextBridge.exposeInMainWorld('yaml', {
    load: (content, options) => {
      try {
        return yaml.load(content, options);
      } catch (error) {
        console.error('Error loading YAML:', error);
        throw error;
      }
    },
    dump: (object, options) => {
      try {
        return yaml.dump(object, options);
      } catch (error) {
        console.error('Error dumping YAML:', error);
        throw error;
      }
    }
  });
} else {
  console.error('js-yaml no está disponible. Instala las dependencias con: npm install');
  contextBridge.exposeInMainWorld('yaml', {
    load: () => {
      throw new Error('js-yaml no está instalado. Ejecuta: npm install');
    },
    dump: () => {
      throw new Error('js-yaml no está instalado. Ejecuta: npm install');
    }
  });
}

