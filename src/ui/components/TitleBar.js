export class TitleBar {
  constructor() {
    this.isMaximized = false;
    this.init();
  }

  async init() {
    // Esperar a que electronAPI esté disponible
    await this.waitForElectronAPI();
    
    // Verificar estado inicial
    if (window.electronAPI) {
      try {
        this.isMaximized = await window.electronAPI.windowIsMaximized();
        this.updateMaximizeIcon();
      } catch (error) {
        console.error('Error checking window state:', error);
      }
    }

    // Configurar event listeners
    this.setupEventListeners();
    
    // Escuchar eventos de maximizar/restaurar
    if (window.electronAPI) {
      window.electronAPI.onWindowMaximize(() => {
        this.isMaximized = true;
        this.updateMaximizeIcon();
      });
      
      window.electronAPI.onWindowUnmaximize(() => {
        this.isMaximized = false;
        this.updateMaximizeIcon();
      });
    }
  }

  async waitForElectronAPI() {
    let attempts = 0;
    while (!window.electronAPI && attempts < 50) {
      await new Promise(resolve => setTimeout(resolve, 100));
      attempts++;
    }
  }

  setupEventListeners() {
    const minimizeBtn = document.getElementById('title-bar-minimize');
    const maximizeBtn = document.getElementById('title-bar-maximize');
    const closeBtn = document.getElementById('title-bar-close');

    if (minimizeBtn) {
      minimizeBtn.addEventListener('click', async () => {
        if (window.electronAPI) {
          await window.electronAPI.windowMinimize();
        }
      });
    }

    if (maximizeBtn) {
      maximizeBtn.addEventListener('click', async () => {
        if (window.electronAPI) {
          await window.electronAPI.windowMaximize();
        }
      });
    }

    if (closeBtn) {
      closeBtn.addEventListener('click', async () => {
        if (window.electronAPI) {
          await window.electronAPI.windowClose();
        }
      });
    }
  }

  updateMaximizeIcon() {
    const maximizeBtn = document.getElementById('title-bar-maximize');
    if (!maximizeBtn) return;

    const maximizeIcon = maximizeBtn.querySelector('.maximize-icon');
    const restoreIcon = maximizeBtn.querySelector('.restore-icon');

    if (this.isMaximized) {
      if (maximizeIcon) maximizeIcon.style.display = 'none';
      if (restoreIcon) restoreIcon.style.display = 'block';
    } else {
      if (maximizeIcon) maximizeIcon.style.display = 'block';
      if (restoreIcon) restoreIcon.style.display = 'none';
    }
  }
}

