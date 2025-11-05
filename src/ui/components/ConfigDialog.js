import { Strings } from '../../i18n/strings.js';

export class ConfigDialog {
  constructor(container, props) {
    this.container = container;
    this.props = props;
    this.listeners = [];
    this.render();
  }

  cleanup() {
    // Limpiar todos los listeners almacenados
    this.listeners.forEach(({ element, event, handler }) => {
      if (element && element.removeEventListener) {
        element.removeEventListener(event, handler);
      }
    });
    this.listeners = [];
  }

  addEventListener(element, event, handler) {
    if (element && element.addEventListener) {
      element.addEventListener(event, handler);
      this.listeners.push({ element, event, handler });
    }
  }

  render() {
    // Limpiar listeners anteriores antes de renderizar
    this.cleanup();
    
    const { show, onDismiss, spectralPath, onSpectralPathChange, onShowSpectralChooser, language, onLanguageChange, config, licenseStatus, onOpenLicense, isNTT = false } = this.props;

    if (!show) {
      this.container.style.display = 'none';
      // Asegurar que el diálogo se cierre si está abierto
      const existingDialog = this.container.querySelector('md-dialog');
      if (existingDialog) {
        if (existingDialog.open) {
          existingDialog.close();
        }
        // Forzar la eliminación del atributo open
        existingDialog.removeAttribute('open');
      }
      // Asegurar que el contenedor no tenga pointer-events bloqueados
      this.container.style.pointerEvents = 'none';
      return;
    }

    this.container.style.display = 'flex';
    this.container.style.pointerEvents = 'auto';
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    this.container.setAttribute('data-theme', currentTheme);
    
    // Renderizar estado de licencia
    let licenseSection = '';
    if (licenseStatus) {
      const { valid, trial, daysLeft, trialUsed } = licenseStatus;
      let licenseIcon = 'check_circle';
      let licenseText = '';
      let licenseClass = 'license-status-active';
      
      if (valid) {
        licenseIcon = 'verified';
        licenseText = Strings.get(language, 'license.activated');
      } else if (trial && daysLeft > 0) {
        licenseIcon = 'schedule';
        licenseText = Strings.get(language, 'license.trialActive', daysLeft);
        licenseClass = 'license-status-trial';
      } else if (trialUsed) {
        licenseIcon = 'error';
        licenseText = Strings.get(language, 'license.trialUsed');
        licenseClass = 'license-status-expired';
      } else {
        licenseIcon = 'info';
        licenseText = Strings.get(language, 'license.notActivated');
        licenseClass = 'license-status-inactive';
      }
      
      licenseSection = `
        <div class="config-section license-section">
          <div class="license-status-container">
            <div class="license-status ${licenseClass}">
              <span class="material-symbols-outlined license-icon">${licenseIcon}</span>
              <div class="license-info">
                <span class="license-label">${Strings.get(language, 'license.status')}</span>
                <span class="license-text">${licenseText}</span>
              </div>
              ${!valid ? `
                <md-text-button id="activate-license-btn" class="license-action-btn">
                  <span slot="icon" class="material-symbols-outlined">key</span>
                  ${Strings.get(language, 'license.have')}
                </md-text-button>
              ` : `
                <md-text-button id="change-license-btn" class="license-action-btn">
                  <span slot="icon" class="material-symbols-outlined">edit</span>
                  ${Strings.get(language, 'license.change')}
                </md-text-button>
              `}
            </div>
          </div>
        </div>
      `;
    }
    
    this.container.innerHTML = `
      <md-dialog id="config-dialog" open style="margin: auto; position: relative; max-width: 600px; width: 90%;">
        <div slot="headline">
          <div style="display: flex; align-items: center; gap: 10px;">
            <span class="material-symbols-outlined" style="font-size: 24px;">settings</span>
            <span style="font-size: 1.25rem; font-weight: 500;">${Strings.get(language, 'config.title')}</span>
          </div>
        </div>
        <form slot="content" id="config-form">
          ${licenseSection}
          
          ${isNTT ? `
          <div class="config-section">
            <div class="section-header">
              <span class="material-symbols-outlined">auto_awesome</span>
              <h3>Spectral</h3>
            </div>
            <div class="form-group">
              <md-filled-text-field 
                id="spectral-path-input" 
                label="${Strings.get(language, 'spectral.selectFolder')}"
                value="${this.escapeHtml(spectralPath || '')}"
                style="width: 100%; margin-bottom: 12px;"
                supporting-text="${spectralPath ? '' : 'Selecciona la carpeta donde está instalado Spectral'}"
              ></md-filled-text-field>
              <md-filled-button id="spectral-chooser-btn" type="button" style="width: 100%;">
                <span slot="icon" class="material-symbols-outlined">folder_open</span>
                ${Strings.get(language, 'export.selectFolder')}
              </md-filled-button>
            </div>
          </div>
          ` : ''}
          
          <div class="config-section">
            <div class="section-header">
              <span class="material-symbols-outlined">language</span>
              <h3>${Strings.get(language, 'menu.language')}</h3>
            </div>
            <div class="form-group">
              <md-filled-select id="language-select" label="${Strings.get(language, 'menu.language')}" style="width: 100%;">
                <md-select-option value="es" ${language === 'es' ? 'selected' : ''}>
                  <span slot="headline">Español</span>
                </md-select-option>
                <md-select-option value="en" ${language === 'en' ? 'selected' : ''}>
                  <span slot="headline">English</span>
                </md-select-option>
                <md-select-option value="ca" ${language === 'ca' ? 'selected' : ''}>
                  <span slot="headline">Català</span>
                </md-select-option>
              </md-filled-select>
            </div>
          </div>
        </form>
        <div slot="actions">
          <md-text-button id="close-btn">${Strings.get(language, 'config.close')}</md-text-button>
          <md-filled-button id="save-btn">
            <span slot="icon" class="material-symbols-outlined">save</span>
            ${Strings.get(language, 'config.save')}
          </md-filled-button>
        </div>
      </md-dialog>
    `;

    // Event listeners - usar addEventListener helper para tracking
    const spectralPathInput = this.container.querySelector('md-filled-text-field#spectral-path-input');
    if (spectralPathInput) {
      const inputHandler = (e) => {
        onSpectralPathChange(spectralPathInput.value);
      };
      this.addEventListener(spectralPathInput, 'input', inputHandler);
    }

    const spectralChooserBtn = this.container.querySelector('#spectral-chooser-btn');
    if (spectralChooserBtn) this.addEventListener(spectralChooserBtn, 'click', onShowSpectralChooser);
    
    const languageSelect = this.container.querySelector('md-filled-select#language-select');
    if (languageSelect) {
      const changeHandler = (e) => {
        const value = languageSelect.value;
        if (value) {
          onLanguageChange(value);
        }
      };
      this.addEventListener(languageSelect, 'change', changeHandler);
    }

    const saveBtn = this.container.querySelector('#save-btn');
    if (saveBtn) {
      this.addEventListener(saveBtn, 'click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        const dialog = this.container.querySelector('md-dialog');
        if (dialog) {
          dialog.close();
        }
        // onDismiss se llamará desde el handler de close
      });
    }

    const closeBtn = this.container.querySelector('#close-btn');
    if (closeBtn) {
      this.addEventListener(closeBtn, 'click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        const dialog = this.container.querySelector('md-dialog');
        if (dialog) {
          dialog.close();
        }
        // onDismiss se llamará desde el handler de close
      });
    }
    
    // Botón de activar licencia
    const activateLicenseBtn = this.container.querySelector('#activate-license-btn');
    if (activateLicenseBtn && onOpenLicense) {
      this.addEventListener(activateLicenseBtn, 'click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        onOpenLicense();
      });
    }
    
    // Botón de cambiar licencia
    const changeLicenseBtn = this.container.querySelector('#change-license-btn');
    if (changeLicenseBtn && onOpenLicense) {
      this.addEventListener(changeLicenseBtn, 'click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        onOpenLicense();
      });
    }
    
    const dialog = this.container.querySelector('md-dialog');
    if (dialog) {
      const closeHandler = () => {
        // Asegurar que el contenedor se oculte cuando el diálogo se cierra
        this.container.style.display = 'none';
        // Forzar la eliminación del atributo open para asegurar que el overlay se limpie
        dialog.removeAttribute('open');
        // Pequeño delay para asegurar que el overlay se limpie completamente
        setTimeout(() => {
          onDismiss();
        }, 100);
      };
      this.addEventListener(dialog, 'close', closeHandler);
      
      // Manejar tecla ESC
      const escapeHandler = (e) => {
        if (e.key === 'Escape' && show) {
          e.preventDefault();
          e.stopPropagation();
          dialog.close();
        }
      };
      this.addEventListener(document, 'keydown', escapeHandler);
      
      // Close on overlay click - asegurar que solo se cierre si se hace clic en el overlay
      const overlayClickHandler = (e) => {
        // Solo cerrar si se hace clic directamente en el contenedor (overlay), no en el diálogo
        if (e.target === this.container) {
          e.preventDefault();
          e.stopPropagation();
          dialog.close();
        }
      };
      this.addEventListener(this.container, 'click', overlayClickHandler);
    }
  }

  escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  updateProps(newProps) {
    this.props = { ...this.props, ...newProps };
    this.render();
  }
}

