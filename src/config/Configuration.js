export class Configuration {
  constructor() {
    this.spectralPath = '';
    this.language = 'es';
    this.dontShowSpectralDialog = false;
    this.loadConfig();
  }

  async waitForElectronAPI() {
    let attempts = 0;
    while (!window.electronAPI && attempts < 50) {
      await new Promise(resolve => setTimeout(resolve, 100));
      attempts++;
    }
    if (!window.electronAPI) {
      throw new Error('electronAPI no está disponible');
    }
  }

  async loadConfig() {
    try {
      await this.waitForElectronAPI();
      this.spectralPath = await window.electronAPI.storeGet('config.spectralPath') || '';
      this.language = await window.electronAPI.storeGet('config.language') || 'es';
      this.dontShowSpectralDialog = await window.electronAPI.storeGet('config.dontShowSpectralDialog') || false;
    } catch (error) {
      console.error('Error loading config:', error);
      this.spectralPath = '';
      this.language = 'es';
      this.dontShowSpectralDialog = false;
    }
  }

  isSpectralPathSet() {
    return this.spectralPath && this.spectralPath.trim() !== '';
  }

  async setSpectralPath(path) {
    await this.waitForElectronAPI();
    this.spectralPath = path;
    await window.electronAPI.storeSet('config.spectralPath', path);
  }

  async setLanguage(lang) {
    await this.waitForElectronAPI();
    this.language = lang;
    await window.electronAPI.storeSet('config.language', lang);
  }

  async setDontShowSpectralDialog(value) {
    await this.waitForElectronAPI();
    this.dontShowSpectralDialog = value;
    await window.electronAPI.storeSet('config.dontShowSpectralDialog', value);
  }
}

