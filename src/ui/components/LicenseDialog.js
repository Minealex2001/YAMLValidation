import { Strings } from '../../i18n/strings.js';

export class LicenseDialog {
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
    
    const { show, onDismiss, licenseInput, onLicenseInputChange, licenseError, onActivate, onTrial, trialActive, daysLeft, trialUsed } = this.props;
    const language = this.props.language || 'es';

    if (!show) {
      this.container.style.display = 'none';
      return;
    }

    this.container.style.display = 'flex';
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    this.container.setAttribute('data-theme', currentTheme);
    this.container.innerHTML = `
      <md-dialog id="license-dialog" open style="margin: auto; position: relative;">
        <div slot="headline">${Strings.get(language, 'license.title')}</div>
        <form slot="content" id="license-form">
          <p style="margin-bottom: 24px; color: var(--md-sys-color-on-surface-variant);">
            ${Strings.get(language, 'license.intro')}
          </p>
          <div class="form-group">
            <md-filled-text-field 
              id="license-key-input" 
              label="${Strings.get(language, 'license.key')}"
              value="${this.escapeHtml(licenseInput)}"
              type="password"
              ${licenseError ? `supporting-text="${this.escapeHtml(licenseError)}" error` : ''}
            ></md-filled-text-field>
          </div>
          ${trialActive ? `
            <md-filled-tonal-chip>
              <span slot="icon" class="material-symbols-outlined">schedule</span>
              ${Strings.get(language, 'license.trialActive', daysLeft)}
            </md-filled-tonal-chip>
          ` : ''}
          ${trialUsed && !trialActive ? `
            <md-filled-tonal-chip>
              <span slot="icon" class="material-symbols-outlined">warning</span>
              ${Strings.get(language, 'license.trialUsed')}
            </md-filled-tonal-chip>
          ` : ''}
        </form>
        <div slot="actions">
          ${onTrial && !trialUsed ? `
            <md-text-button id="trial-btn">${Strings.get(language, 'license.trial')}</md-text-button>
          ` : ''}
          <md-filled-button id="activate-btn">${Strings.get(language, 'license.activate')}</md-filled-button>
        </div>
      </md-dialog>
    `;

    // Event listeners - usar addEventListener helper para tracking
    const licenseInputEl = this.container.querySelector('md-filled-text-field#license-key-input');
    if (licenseInputEl) {
      const inputHandler = (e) => {
        onLicenseInputChange(licenseInputEl.value);
      };
      this.addEventListener(licenseInputEl, 'input', inputHandler);
    }

    const activateBtn = this.container.querySelector('#activate-btn');
    if (activateBtn) this.addEventListener(activateBtn, 'click', onActivate);
    
    if (onTrial && !trialUsed) {
      const trialBtn = this.container.querySelector('#trial-btn');
      if (trialBtn) this.addEventListener(trialBtn, 'click', onTrial);
    }

    const dialog = this.container.querySelector('md-dialog');
    if (dialog) {
      const closeHandler = () => {
        if (dialog.returnValue !== 'cancel') {
          onDismiss();
        }
      };
      this.addEventListener(dialog, 'close', closeHandler);
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

