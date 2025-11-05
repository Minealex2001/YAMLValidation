const SECRET = 'VALIDADOR-YAML-2025';
const SECRET_NTT = 'VALIDADOR-YAML-2025-NTTDATA';
const TRIAL_PREFIX = 'TRIAL:';
const NTT_PREFIX = 'NTT:';

export class LicenseManager {
  static async waitForElectronAPI() {
    // Esperar a que electronAPI esté disponible
    let attempts = 0;
    while (!window.electronAPI && attempts < 50) {
      await new Promise(resolve => setTimeout(resolve, 100));
      attempts++;
    }
    if (!window.electronAPI) {
      throw new Error('electronAPI no está disponible. Asegúrate de que el preload script se esté cargando correctamente.');
    }
  }

  static async isLicenseValid() {
    try {
      await this.waitForElectronAPI();
      const licenseKey = await window.electronAPI.storeGet('license.key');
      if (!licenseKey) return false;
      return await this.validateKey(licenseKey);
    } catch (error) {
      console.error('Error checking license:', error);
      return false;
    }
  }

  static async saveLicenseKey(key) {
    await this.waitForElectronAPI();
    const trimmedKey = key.trim();
    await window.electronAPI.storeSet('license.key', trimmedKey);
    
    // Guardar si es licencia de NTT
    const isNTT = trimmedKey.startsWith(NTT_PREFIX);
    await window.electronAPI.storeSet('license.isNTT', isNTT);
  }

  static async removeLicense() {
    await this.waitForElectronAPI();
    await window.electronAPI.storeDelete('license.key');
    await window.electronAPI.storeDelete('license.isNTT');
  }

  static async generateKey(randomParam, isNTT = false) {
    const secret = isNTT ? SECRET_NTT : SECRET;
    const input = `${secret}-${randomParam}`;
    const encoder = new TextEncoder();
    const data = encoder.encode(input);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashBase64 = btoa(String.fromCharCode(...hashArray));
    const prefix = isNTT ? NTT_PREFIX : '';
    return `${prefix}${hashBase64}:${randomParam}`;
  }

  static async validateKey(key) {
    const parts = key.split(':');
    if (parts.length < 2) return false;
    
    // Detectar si es licencia de NTT
    const isNTT = key.startsWith(NTT_PREFIX);
    let hashBase64, randomParam;
    
    if (isNTT) {
      // Formato: NTT:hashBase64:randomParam
      if (parts.length !== 3) return false;
      [, hashBase64, randomParam] = parts;
    } else {
      // Formato: hashBase64:randomParam
      [hashBase64, randomParam] = parts;
    }
    
    const expected = await this.generateKey(randomParam, isNTT);
    return key === expected;
  }

  static async isNTTLicense() {
    try {
      await this.waitForElectronAPI();
      const licenseKey = await window.electronAPI.storeGet('license.key');
      if (!licenseKey) return false;
      if (!await this.validateKey(licenseKey)) return false;
      return licenseKey.startsWith(NTT_PREFIX);
    } catch (error) {
      console.error('Error checking NTT license:', error);
      return false;
    }
  }

  static async saveTrialStartDate(date) {
    await this.waitForElectronAPI();
    const encrypted = this.encrypt(date);
    const encoded = btoa(`${TRIAL_PREFIX}${encrypted}`);
    await window.electronAPI.storeSet('license.trial', encoded);
  }

  static async getTrialStartDate() {
    try {
      await this.waitForElectronAPI();
      const encoded = await window.electronAPI.storeGet('license.trial');
      if (!encoded) return null;
      
      const decoded = atob(encoded);
      if (!decoded.startsWith(TRIAL_PREFIX)) return null;
      
      return this.decrypt(decoded.substring(TRIAL_PREFIX.length));
    } catch (error) {
      console.error('Error getting trial start date:', error);
      return null;
    }
  }

  static encrypt(input) {
    const key = new TextEncoder().encode(SECRET);
    const inputBytes = new TextEncoder().encode(input);
    const out = new Uint8Array(inputBytes.length);
    
    for (let i = 0; i < inputBytes.length; i++) {
      out[i] = inputBytes[i] ^ key[i % key.length];
    }
    
    return btoa(String.fromCharCode(...out));
  }

  static decrypt(input) {
    const key = new TextEncoder().encode(SECRET);
    const inputBytes = Uint8Array.from(atob(input), c => c.charCodeAt(0));
    const out = new Uint8Array(inputBytes.length);
    
    for (let i = 0; i < inputBytes.length; i++) {
      out[i] = inputBytes[i] ^ key[i % key.length];
    }
    
    return new TextDecoder().decode(out);
  }
}

