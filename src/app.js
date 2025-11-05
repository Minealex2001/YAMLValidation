// Use yaml from window (exposed by preload)
const yaml = window.yaml;

import { ValidatorCore } from './core/ValidatorCore.js';
import { ValidationLogger } from './validation/ValidationLogger.js';
import { OperationIdValidation } from './validation/rules/OperationIdValidation.js';
import { AbsisOperationValidation } from './validation/rules/AbsisOperationValidation.js';
import { CertificationValidation } from './validation/rules/CertificationValidation.js';
import { TypologyValidation } from './validation/rules/TypologyValidation.js';
import { Codigo2xxValidation } from './validation/rules/Codigo2xxValidation.js';
import { Codigo400Validation } from './validation/rules/Codigo400Validation.js';
import { RequestBodyValidation } from './validation/rules/RequestBodyValidation.js';
import { LicenseManager } from './licensing/LicenseManager.js';
import { Configuration } from './config/Configuration.js';
import { AppConfig } from './core/AppConfig.js';
import { Strings } from './i18n/strings.js';
import { MainCard } from './ui/components/MainCard.js';
import { ResultsTabs } from './ui/components/ResultsTabs.js';
import { ErrorBanner } from './ui/components/ErrorBanner.js';
import { AppFooter } from './ui/components/AppFooter.js';
import { ThemeToggle } from './ui/components/ThemeToggle.js';
import { ConfigDialog } from './ui/components/ConfigDialog.js';
import { LicenseDialog } from './ui/components/LicenseDialog.js';
import { TrialExpiredDialog } from './ui/components/TrialExpiredDialog.js';
import { SpectralExportedDialog } from './ui/components/SpectralExportedDialog.js';
import { SpectralDialog } from './ui/components/SpectralDialog.js';
import { TitleBar } from './ui/components/TitleBar.js';
import { HelpDialog } from './ui/components/HelpDialog.js';

class App {
  constructor() {
    this.yamlPath = '';
    this.spectralPath = '';
    this.language = 'es';
    this.spectralOutput = '';
    this.exportPath = '';
    this.errorBanner = null;
    this.showYamlChooser = false;
    this.showSpectralChooser = false;
    this.showExportChooser = false;
    this.showConfigDialog = false;
    this.showLicenseDialog = false;
    this.showTrialExpiredDialog = false;
    this.showSpectralExportedDialog = false;
    this.showSpectralDialog = false;
    this.showHelpDialog = false;
    this.skipSpectralValidation = false;
    this.selectedTab = 0;
    this.licenseInput = '';
    this.licenseError = null;
    
    // Render throttling
    this.renderThrottle = {};
    this.renderTimeout = null;
    
    this.logger = new ValidationLogger();
    this.config = new Configuration();
    
    this.init();
  }
  
  // Throttle render calls para evitar renderizado excesivo
  throttleRender(componentName, renderFn, delay = 100) {
    if (this.renderThrottle[componentName]) {
      clearTimeout(this.renderThrottle[componentName]);
    }
    this.renderThrottle[componentName] = setTimeout(async () => {
      await renderFn();
      delete this.renderThrottle[componentName];
    }, delay);
  }

  async init() {
    await this.config.loadConfig();
    this.spectralPath = this.config.spectralPath;
    this.language = this.config.language;
    AppConfig.language = this.language;

    // Aplicar paleta de colores según tipo de licencia
    await this.applyLicenseTheme();

    // Initialize UI
    await this.setupUI();
    
    // Check license
    await this.checkLicense();
    
    // Si hay ruta de Spectral configurada, ejecutar git pull (después de que UI esté lista)
    // Usar setTimeout para asegurar que el errorBanner esté inicializado
    setTimeout(async () => {
      await this.checkSpectralUpdate();
    }, 500);
  }
  
  async checkSpectralUpdate() {
    if (this.spectralPath && this.spectralPath.trim() !== '') {
      try {
        // Verificar si la ruta existe
        const exists = await window.electronAPI.exists(this.spectralPath);
        if (exists) {
          // Determinar el directorio donde ejecutar git pull
          // spectralPath debería ser la carpeta de Spectral (seleccionada con showOpenDirectory)
          // Ejecutar git pull dentro de esa carpeta
          const spectralDir = this.spectralPath;
          
          // Ejecutar git pull en el directorio de Spectral
          const result = await window.electronAPI.gitPull(spectralDir);
          if (result.success) {
            // Verificar si hubo actualizaciones (git pull muestra "Already up to date" si no hay cambios)
            const output = result.output || '';
            const hasUpdates = !output.includes('Already up to date') && !output.includes('Already up-to-date');
            
            if (hasUpdates) {
              // Mostrar notificación de éxito
              const updateMessage = output.trim().split('\n')[0] || 'Spectral actualizado correctamente';
              this.errorBanner.show(`Spectral actualizado: ${updateMessage}`);
              // Ocultar después de 5 segundos
              setTimeout(() => {
                this.errorBanner.hide();
              }, 5000);
            }
          } else {
            // Si falla, no mostrar error (puede ser que no sea un repositorio git o no haya conexión)
            console.log('Git pull no pudo ejecutarse en:', spectralDir, result.error);
          }
        }
      } catch (error) {
        // Si hay error, no mostrar (puede ser que no sea un repositorio git)
        console.log('Error verificando actualización de Spectral:', error);
      }
    }
  }

  async applyLicenseTheme() {
    const isNTT = await LicenseManager.isNTTLicense();
    if (isNTT) {
      document.documentElement.setAttribute('data-license', 'ntt');
      document.body.setAttribute('data-license', 'ntt');
    } else {
      document.documentElement.removeAttribute('data-license');
      document.body.removeAttribute('data-license');
    }
  }

  async setupUI() {
    // Title bar
    this.titleBar = new TitleBar();
    this.titleBar.init().catch(console.error);
    
    // Theme toggle
    const themeToggleEl = document.getElementById('theme-toggle');
    if (themeToggleEl) {
      this.themeToggle = new ThemeToggle(themeToggleEl);
      this.themeToggle.init().catch(console.error);
      
      // Escuchar cambios de tema para actualizar componentes con branding NTT
      window.addEventListener('theme-changed', async () => {
        // Actualizar MainCard si tiene branding NTT
        if (this.mainCard) {
          const isNTT = await LicenseManager.isNTTLicense();
          this.mainCard.updateProps({ isNTT });
        }
        // Actualizar Footer si tiene branding NTT
        if (this.footer) {
          await this.footer.update();
        }
      });
    }
    
    // Error banner
    const errorBannerEl = document.getElementById('error-banner');
    this.errorBanner = new ErrorBanner(errorBannerEl);

    // Main card
    const mainCardContainer = document.createElement('div');
    document.querySelector('.container').prepend(mainCardContainer);
    const isNTT = await LicenseManager.isNTTLicense();
    this.mainCard = new MainCard(mainCardContainer, {
      yamlPath: this.yamlPath,
      onYamlPathChange: (path) => { this.yamlPath = path; },
      onYamlChooser: () => this.handleYamlChooser(),
      language: this.language,
      onValidate: () => this.handleValidate(),
      onSpectral: () => this.handleSpectral(),
      onExport: () => this.handleExport(),
      onConfig: () => { this.showConfigDialog = true; this.renderConfigDialog(); },
      onHelp: () => { this.showHelpDialog = true; this.renderHelpDialog(); },
      spectralPath: this.spectralPath,
      showYamlChooser: this.showYamlChooser,
      showSpectralChooser: this.showSpectralChooser,
      showExportChooser: this.showExportChooser,
      isNTT: isNTT,
      Strings: Strings
    });

    // Results tabs
    const resultsTabsContainer = document.createElement('div');
    document.querySelector('.container').appendChild(resultsTabsContainer);
    const isNTTResults = await LicenseManager.isNTTLicense();
    this.resultsTabs = new ResultsTabs(resultsTabsContainer, {
      selectedTab: this.selectedTab,
      onTabChange: (tab) => { 
        // Solo actualizar si realmente cambió el tab
        if (this.selectedTab !== tab) {
          this.selectedTab = tab;
          // No llamar renderResultsTabs aquí porque updateProps ya lo maneja
          // Solo actualizar las props sin re-renderizar
          if (this.resultsTabs) {
            this.resultsTabs.updateProps({ selectedTab: tab });
          }
        }
      },
      logger: this.logger,
      spectralOutput: this.spectralOutput,
      language: this.language,
      isNTT: isNTTResults,
      Strings: Strings
    });

    // Footer
    const footerContainer = document.getElementById('app-footer');
    if (footerContainer) {
      this.footer = new AppFooter(footerContainer, {
        language: this.language,
        onLicenseCheck: async () => {
          return await LicenseManager.isNTTLicense();
        }
      });
    }

    // Dialogs
    this.renderConfigDialog();
    this.renderLicenseDialog().catch(console.error);
    this.renderHelpDialog();
    this.renderTrialExpiredDialog();
    this.renderSpectralExportedDialog();
    this.renderSpectralDialog();
  }

  async checkLicense() {
    const licenseValid = await LicenseManager.isLicenseValid();
    const trialStartDate = await LicenseManager.getTrialStartDate();
    
    if (trialStartDate) {
      const startDate = new Date(trialStartDate);
      const now = new Date();
      const daysDiff = Math.floor((now - startDate) / (1000 * 60 * 60 * 24));
      const daysLeft = 7 - daysDiff;
      
      if (daysLeft <= 0 && !licenseValid) {
        this.showTrialExpiredDialog = true;
        this.renderTrialExpiredDialog();
      }
    } else if (!licenseValid) {
      this.showLicenseDialog = true;
      await this.renderLicenseDialog();
    }
  }

  async handleYamlChooser() {
    const result = await window.electronAPI.showOpenFile({
      title: Strings.get(this.language, 'file.open'),
      filters: [{ name: 'YAML', extensions: ['yaml', 'yml'] }]
    });

    if (result.canceled === false && result.filePaths.length > 0) {
      this.yamlPath = result.filePaths[0];
      this.mainCard.updateProps({ yamlPath: this.yamlPath });
    }
  }

  async handleValidate() {
    this.logger.clear();
    this.spectralOutput = '';
    this.errorBanner.hide();

    const isNTT = await LicenseManager.isNTTLicense();
    
    // Solo ejecutar Spectral si es licencia de NTT
    if (isNTT) {
      if (this.spectralPath === '' && !this.config.dontShowSpectralDialog && !this.skipSpectralValidation) {
        this.showSpectralDialog = true;
        this.renderSpectralDialog();
        return;
      }
    }

    const rules = [
      new OperationIdValidation(),
      new AbsisOperationValidation(),
      new CertificationValidation(),
      new TypologyValidation(),
      new Codigo2xxValidation(),
      new RequestBodyValidation(),
      new Codigo400Validation()
    ];

    const validator = new ValidatorCore(
      this.yamlPath,
      this.logger,
      rules,
      null,
      this.language,
      isNTT ? this.spectralPath : null // Solo pasar spectralPath si es NTT
    );

    try {
      let result;
      if (isNTT) {
        result = await validator.runAllValidationsParallel();
      } else {
        // Si no es NTT, solo ejecutar validaciones internas
        const internalResult = await validator.runInternalValidations();
        result = {
          internal: internalResult,
          spectral: ''
        };
      }
      
      if (!result.internal.success) {
        this.errorBanner.show(result.internal.errorMessage);
      }
      this.spectralOutput = result.spectral || '';
      // Actualizar ResultsTabs con el nuevo spectralOutput y asegurar que isNTT esté actualizado
      if (this.resultsTabs) {
        const isNTTCurrent = await LicenseManager.isNTTLicense();
        this.resultsTabs.updateProps({
          spectralOutput: this.spectralOutput,
          isNTT: isNTTCurrent
        });
      }
      this.renderResultsTabs();
    } catch (error) {
      this.errorBanner.show(`Error: ${error.message}`);
    }
  }

  async handleSpectral() {
    const isNTT = await LicenseManager.isNTTLicense();
    if (!isNTT) {
      return; // No mostrar funcionalidad de Spectral si no es licencia de NTT
    }
    
    if (this.yamlPath === '') {
      this.errorBanner.show(Strings.get(this.language, 'error.noYaml'));
      return;
    }
    if (this.spectralPath === '') {
      this.errorBanner.show(Strings.get(this.language, 'spectral.required'));
      return;
    }
    this.showSpectralChooser = true;
    await this.handleSpectralExport();
  }

  async handleSpectralExport() {
    const result = await window.electronAPI.showOpenDirectory({
      title: Strings.get(this.language, 'export.selectFolder')
    });

    if (result.canceled === false && result.filePaths.length > 0) {
      const folder = result.filePaths[0];
      try {
        // Read YAML to get title
        let nombreMicro = 'microservicio';
        if (this.yamlPath) {
          const yamlResult = await window.electronAPI.readFile(this.yamlPath);
          if (yamlResult.success) {
            const data = yaml.load(yamlResult.content);
            const title = data?.info?.title;
            if (title) {
              nombreMicro = this.sanitizeFilename(title);
            }
          }
        }

        // Copy YAML to destination
        const destYamlFile = `${folder}/${nombreMicro}.yaml`;
        await window.electronAPI.copyFile(this.yamlPath, destYamlFile);

        // Run Spectral
        const rules = [
          new OperationIdValidation(),
          new AbsisOperationValidation(),
          new CertificationValidation(),
          new TypologyValidation(),
          new Codigo2xxValidation(),
          new RequestBodyValidation(),
          new Codigo400Validation()
        ];

        const validator = new ValidatorCore(
          destYamlFile,
          this.logger,
          rules,
          null,
          this.language,
          this.spectralPath
        );

        const now = new Date();
        const fechaHora = now.toISOString().replace(/[:.]/g, '-').slice(0, -5);
        const outputTxtFile = `${folder}/${nombreMicro}-spectral-${fechaHora}.txt`;
        
        const exportResult = await validator.exportSpectralToFile(outputTxtFile);
        
        if (exportResult.success) {
          this.showSpectralExportedDialog = true;
          this.renderSpectralExportedDialog();
        } else {
          this.errorBanner.show(Strings.get(this.language, 'export.error') + ' ' + (exportResult.errorMessage || ''));
        }
      } catch (error) {
        this.errorBanner.show(`Error durante la exportación: ${error.message}`);
      }
    }
  }

  async handleExport() {
    const result = await window.electronAPI.showSaveFile({
      title: Strings.get(this.language, 'export.dialogTitle'),
      defaultPath: Strings.get(this.language, 'export.defaultFile'),
      filters: [{ name: 'Text', extensions: ['txt'] }]
    });

    if (result.canceled === false && result.filePath) {
      try {
        let content = '';
        this.logger.logs.forEach(log => {
          content += `[${log.level}] ${log.message}\n`;
        });
        if (this.spectralOutput) {
          content += `\n--- ${Strings.get(this.language, 'tab.spectralValidations')} ---\n`;
          content += this.spectralOutput;
        }

        await window.electronAPI.writeFile(result.filePath, content);
        this.errorBanner.show(Strings.get(this.language, 'export.success') + ' ' + result.filePath);
      } catch (error) {
        this.errorBanner.show(Strings.get(this.language, 'export.error') + ' ' + error.message);
      }
    }
  }

  async renderConfigDialog() {
    const container = document.getElementById('config-dialog');
    if (!container) return;
    
    // Obtener información de la licencia
    const licenseValid = await LicenseManager.isLicenseValid();
    const trialStartDate = await LicenseManager.getTrialStartDate();
    const isNTT = await LicenseManager.isNTTLicense();
    let licenseStatus = {
      valid: licenseValid,
      trial: false,
      daysLeft: 0,
      trialUsed: false
    };
    
    if (trialStartDate) {
      const startDate = new Date(trialStartDate);
      const now = new Date();
      const daysDiff = Math.floor((now - startDate) / (1000 * 60 * 60 * 24));
      const daysLeft = 7 - daysDiff;
      licenseStatus.trial = !licenseValid && daysLeft > 0;
      licenseStatus.daysLeft = daysLeft > 0 ? daysLeft : 0;
      licenseStatus.trialUsed = true;
    }
    
    this.configDialog = new ConfigDialog(container, {
      show: this.showConfigDialog,
      onDismiss: () => { this.showConfigDialog = false; this.renderConfigDialog(); },
      spectralPath: this.spectralPath,
      onSpectralPathChange: async (path) => { 
        this.spectralPath = path; 
        await this.config.setSpectralPath(path);
      },
      onShowSpectralChooser: async () => {
        const result = await window.electronAPI.showOpenDirectory({
          title: Strings.get(this.language, 'spectral.selectFolder')
        });
        if (result.canceled === false && result.filePaths.length > 0) {
          this.spectralPath = result.filePaths[0];
          await this.config.setSpectralPath(this.spectralPath);
          this.renderConfigDialog();
        }
      },
      language: this.language,
      onLanguageChange: async (lang) => {
        this.language = lang;
        AppConfig.language = lang;
        await this.config.setLanguage(lang);
        this.renderConfigDialog();
        this.mainCard.updateProps({ language: this.language });
        this.resultsTabs.updateProps({ language: this.language });
      },
      config: this.config,
      licenseStatus: licenseStatus,
      onOpenLicense: () => {
        this.showConfigDialog = false;
        this.showLicenseDialog = true;
        this.renderConfigDialog();
        this.renderLicenseDialog();
      },
      isNTT: isNTT
    });
  }

  async renderLicenseDialog() {
    const container = document.getElementById('license-dialog');
    if (!container) return;

    const licenseValid = await LicenseManager.isLicenseValid();
    const trialStartDate = await LicenseManager.getTrialStartDate();
    const daysLeft = trialStartDate ? 7 - Math.floor((new Date() - new Date(trialStartDate)) / (1000 * 60 * 60 * 24)) : 0;
    const trialActive = !licenseValid && trialStartDate && daysLeft > 0;
    const trialUsed = trialStartDate != null;

    this.licenseDialog = new LicenseDialog(container, {
      show: this.showLicenseDialog || (!licenseValid && !trialActive),
      onDismiss: () => { this.showLicenseDialog = false; this.renderLicenseDialog(); },
      licenseInput: this.licenseInput,
      onLicenseInputChange: (input) => { this.licenseInput = input; },
      licenseError: this.licenseError,
      onActivate: async () => {
        if (await LicenseManager.validateKey(this.licenseInput)) {
          await LicenseManager.saveLicenseKey(this.licenseInput);
          this.showLicenseDialog = false;
          await this.renderLicenseDialog();
          // Actualizar footer, main card, results tabs, config dialog y paleta de colores si es licencia de NTT
          await this.applyLicenseTheme();
          if (this.footer) {
            await this.footer.update();
          }
          if (this.mainCard) {
            const isNTT = await LicenseManager.isNTTLicense();
            this.mainCard.updateProps({ isNTT });
          }
          if (this.resultsTabs) {
            const isNTT = await LicenseManager.isNTTLicense();
            this.resultsTabs.updateProps({ isNTT });
          }
          // Actualizar config dialog para mostrar/ocultar sección de Spectral
          await this.renderConfigDialog();
        } else {
          this.licenseError = Strings.get(this.language, 'license.invalid');
          await this.renderLicenseDialog();
        }
      },
      onTrial: !trialActive ? async () => {
        if (!trialUsed) {
          const today = new Date().toISOString().split('T')[0];
          await LicenseManager.saveTrialStartDate(today);
          this.showLicenseDialog = false;
          this.renderLicenseDialog();
        }
      } : null,
      trialActive: trialActive,
      daysLeft: daysLeft,
      trialUsed: trialUsed,
      language: this.language
    });
  }

  async renderResultsTabs() {
    // Throttle render para evitar renderizado excesivo
    this.throttleRender('resultsTabs', async () => {
      if (this.resultsTabs) {
        const isNTT = await LicenseManager.isNTTLicense();
        this.resultsTabs.updateProps({
          selectedTab: this.selectedTab,
          logger: this.logger,
          spectralOutput: this.spectralOutput,
          language: this.language,
          isNTT: isNTT
        });
      }
    });
  }

  renderTrialExpiredDialog() {
    const container = document.getElementById('trial-expired-dialog');
    if (!container) return;

    this.trialExpiredDialog = new TrialExpiredDialog(container, {
      show: this.showTrialExpiredDialog,
      onDismiss: () => { this.showTrialExpiredDialog = false; this.renderTrialExpiredDialog(); },
      language: this.language
    });
  }

  renderSpectralExportedDialog() {
    const container = document.getElementById('spectral-exported-dialog');
    if (!container) return;

    this.spectralExportedDialog = new SpectralExportedDialog(container, {
      show: this.showSpectralExportedDialog,
      onDismiss: () => { this.showSpectralExportedDialog = false; this.renderSpectralExportedDialog(); },
      language: this.language
    });
  }

  renderSpectralDialog() {
    const container = document.getElementById('spectral-dialog');
    if (!container) return;

    this.spectralDialog = new SpectralDialog(container, {
      show: this.showSpectralDialog,
      onDismiss: () => { this.showSpectralDialog = false; this.renderSpectralDialog(); },
      onSetPath: async () => {
        const result = await window.electronAPI.showOpenDirectory({
          title: Strings.get(this.language, 'spectral.selectFolder')
        });
        if (result.canceled === false && result.filePaths.length > 0) {
          this.spectralPath = result.filePaths[0];
          await this.config.setSpectralPath(this.spectralPath);
          this.handleValidate();
        }
      },
      onSkip: () => {
        this.skipSpectralValidation = true;
        this.handleValidate();
      },
      onDontShow: async () => {
        await this.config.setDontShowSpectralDialog(true);
        this.skipSpectralValidation = true;
        this.handleValidate();
      },
      language: this.language
    });
  }

  renderHelpDialog() {
    const container = document.getElementById('help-dialog');
    if (!container) return;

    if (!this.helpDialog) {
      this.helpDialog = new HelpDialog(container, {
        show: this.showHelpDialog,
        onDismiss: () => { this.showHelpDialog = false; this.renderHelpDialog(); },
        language: this.language
      });
    } else {
      this.helpDialog.props.show = this.showHelpDialog;
      this.helpDialog.props.language = this.language;
      this.helpDialog.render();
    }
  }

  sanitizeFilename(filename) {
    return filename.replace(/[^a-z0-9]/gi, '_').toLowerCase();
  }
}

// Cache para evitar múltiples llamadas simultáneas a waitForElectronAPI
let electronAPIWaitPromise = null;

// Wait for electronAPI to be available
async function waitForElectronAPI() {
  // Si ya hay una promesa en espera, reutilizarla
  if (electronAPIWaitPromise) {
    return electronAPIWaitPromise;
  }
  
  // Si electronAPI ya está disponible, retornar inmediatamente
  if (window.electronAPI) {
    return Promise.resolve(true);
  }
  
  // Crear nueva promesa de espera
  electronAPIWaitPromise = (async () => {
    let attempts = 0;
    const maxAttempts = 50;
    while (!window.electronAPI && attempts < maxAttempts) {
      await new Promise(resolve => setTimeout(resolve, 100));
      attempts++;
    }
    
    // Limpiar la promesa después de completar
    electronAPIWaitPromise = null;
    
    if (!window.electronAPI) {
      console.error('electronAPI no está disponible. Verifica que el preload script se esté cargando correctamente.');
      return false;
    }
    return true;
  })();
  
  return electronAPIWaitPromise;
}

// Initialize app when DOM is ready and electronAPI is available
async function initializeApp() {
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', async () => {
      console.log('DOM loaded, waiting for electronAPI...');
      const apiReady = await waitForElectronAPI();
      if (apiReady) {
        console.log('Initializing app...');
        try {
          new App();
        } catch (error) {
          console.error('Error initializing app:', error);
        }
      }
    });
  } else {
    console.log('DOM already loaded, waiting for electronAPI...');
    const apiReady = await waitForElectronAPI();
    if (apiReady) {
      console.log('Initializing app...');
      try {
        new App();
      } catch (error) {
        console.error('Error initializing app:', error);
      }
    }
  }
}

initializeApp();

