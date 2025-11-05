import { Strings } from '../../i18n/strings.js';

export class MainCard {
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
    
    const { yamlPath, onYamlPathChange, onYamlChooser, language, onValidate, onSpectral, onExport, onConfig, onHelp, isNTT } = this.props;

    this.container.innerHTML = `
      <md-filled-card style="padding: 32px;">
        ${isNTT ? `
          <div style="display: flex; align-items: center; justify-content: center; margin-bottom: 20px; padding: 12px 0;">
            <div style="font-size: 28px; font-weight: 700; color: var(--md-sys-color-primary); letter-spacing: 3px; text-transform: uppercase; position: relative; padding-bottom: 8px;">
              NTT DATA
              <div style="position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 60px; height: 3px; background: linear-gradient(90deg, transparent, var(--md-sys-color-primary), transparent); border-radius: 2px;"></div>
            </div>
          </div>
        ` : ''}
        <h1>${Strings.get(language, 'app.title')}</h1>
        <div class="form-group">
          <div style="display: flex; gap: 16px; align-items: flex-end; margin-bottom: 8px;">
            <md-filled-text-field 
              id="yaml-path" 
              label="${Strings.get(language, 'file.label')}"
              value="${this.escapeHtml(yamlPath)}"
              style="flex: 1;"
              supporting-text=""
            ></md-filled-text-field>
            <md-filled-button id="yaml-chooser-btn" style="margin-bottom: 8px; min-width: 160px;">
              <span slot="icon" class="material-symbols-outlined">folder_open</span>
              ${Strings.get(language, 'file.open')}
            </md-filled-button>
          </div>
        </div>
        <div class="button-group">
          <md-filled-button id="validate-btn">
            <span slot="icon" class="material-symbols-outlined">check_circle</span>
            ${Strings.get(language, 'validate.button')}
          </md-filled-button>
          ${isNTT ? `
          <md-filled-tonal-button id="spectral-btn">
            <span slot="icon" class="material-symbols-outlined">download</span>
            ${Strings.get(language, 'export.spectral')}
          </md-filled-tonal-button>
          ` : ''}
          <md-outlined-button id="export-btn">
            <span slot="icon" class="material-symbols-outlined">save</span>
            ${Strings.get(language, 'output.export')}
          </md-outlined-button>
          <md-outlined-button id="config-btn">
            <span slot="icon" class="material-symbols-outlined">settings</span>
            ${Strings.get(language, 'config.open')}
          </md-outlined-button>
          <md-outlined-button id="help-btn">
            <span slot="icon" class="material-symbols-outlined">help</span>
            ${Strings.get(language, 'help.button')}
          </md-outlined-button>
        </div>
      </md-filled-card>
    `;

    // Event listeners - usar addEventListener helper para tracking
    const yamlPathInput = this.container.querySelector('md-filled-text-field#yaml-path');
    if (yamlPathInput) {
      const inputHandler = (e) => {
        onYamlPathChange(yamlPathInput.value);
      };
      this.addEventListener(yamlPathInput, 'input', inputHandler);
    }

    const yamlChooserBtn = this.container.querySelector('#yaml-chooser-btn');
    if (yamlChooserBtn) this.addEventListener(yamlChooserBtn, 'click', onYamlChooser);
    
    const validateBtn = this.container.querySelector('#validate-btn');
    if (validateBtn) this.addEventListener(validateBtn, 'click', onValidate);
    
    const spectralBtn = this.container.querySelector('#spectral-btn');
    if (spectralBtn && onSpectral) this.addEventListener(spectralBtn, 'click', onSpectral);
    
    const exportBtn = this.container.querySelector('#export-btn');
    if (exportBtn) this.addEventListener(exportBtn, 'click', onExport);
    
    const configBtn = this.container.querySelector('#config-btn');
    if (configBtn) this.addEventListener(configBtn, 'click', onConfig);
    
    const helpBtn = this.container.querySelector('#help-btn');
    if (helpBtn && onHelp) this.addEventListener(helpBtn, 'click', onHelp);
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

