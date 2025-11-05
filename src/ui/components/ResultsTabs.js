import { Strings } from '../../i18n/strings.js';

export class ResultsTabs {
  constructor(container, props) {
    this.container = container;
    this.props = props;
    this.listeners = [];
    this.isUpdating = false; // Flag para prevenir bucles
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
    
    const { selectedTab, onTabChange, logger, spectralOutput, language, isNTT = false } = this.props;

    const logs = logger.logs || [];
    
    // Si no es licencia de NTT, solo mostrar validaciones de la aplicación
    const showSpectralTab = isNTT;
    const effectiveSelectedTab = showSpectralTab ? selectedTab : 0;
    
    this.container.innerHTML = `
      <md-outlined-card style="padding: 24px;">
        <md-tabs id="results-tabs" active-tab-index="${effectiveSelectedTab}">
          <md-primary-tab id="tab-0">
            <span slot="icon" class="material-symbols-outlined">checklist</span>
            ${Strings.get(language, 'tab.appValidations')}
          </md-primary-tab>
          ${showSpectralTab ? `
          <md-primary-tab id="tab-1">
            <span slot="icon" class="material-symbols-outlined">auto_awesome</span>
            ${Strings.get(language, 'tab.spectralValidations')}
          </md-primary-tab>
          ` : ''}
        </md-tabs>
        <div class="tab-content">
          ${effectiveSelectedTab === 0 ? this.renderAppValidations(logs) : (showSpectralTab ? this.renderSpectralValidations(spectralOutput) : this.renderAppValidations(logs))}
        </div>
      </md-outlined-card>
    `;

    // Tab event listeners - solo responder a cambios del usuario, no programáticos
    const tabs = this.container.querySelector('md-tabs');
    if (tabs) {
      // Esperar a que el componente se haya inicializado completamente
      setTimeout(() => {
        const changeHandler = (e) => {
          // Solo procesar si no estamos actualizando programáticamente
          if (this.isUpdating) {
            return;
          }
          
          const activeTab = tabs.activeTabIndex;
          // Solo llamar onTabChange si el tab realmente cambió
          if (activeTab !== this.props.selectedTab) {
            // Prevenir que el evento se propague mientras procesamos
            this.isUpdating = true;
            onTabChange(activeTab);
            // Permitir eventos después de un breve delay
            setTimeout(() => {
              this.isUpdating = false;
            }, 50);
          }
        };
        this.addEventListener(tabs, 'change', changeHandler);
      }, 0);
    }
  }

  renderAppValidations(logs) {
    if (logs.length === 0) {
      return `
        <div class="empty-state">
          <span class="material-symbols-outlined">info</span>
          <p>No hay validaciones aún.</p>
        </div>
      `;
    }

    return logs.map(log => {
      const level = log.level.toUpperCase();
      let colorClass = '';
      if (level === 'ERROR') colorClass = 'error';
      else if (level === 'WARNING' || level === 'WARN') colorClass = 'warning';
      else if (level === 'SUCCESS' || level === 'OK') colorClass = 'success';
      else if (level === 'INFO') colorClass = 'info';

      return `<div class="log-entry ${colorClass}"><strong>[${level}]</strong> ${this.escapeHtml(log.message)}</div>`;
    }).join('');
  }

  renderSpectralValidations(spectralOutput) {
    if (!spectralOutput || spectralOutput.trim() === '') {
      return `
        <div class="empty-state">
          <span class="material-symbols-outlined">auto_awesome</span>
          <p>No hay resultados de Spectral aún.</p>
        </div>
      `;
    }

    return `<div class="spectral-output" style="white-space: pre-wrap; font-family: 'Roboto Mono', 'Consolas', 'Monaco', monospace; padding: 20px; background: var(--md-sys-color-surface-variant); border-radius: 16px; font-size: 0.875rem; line-height: 1.8; color: var(--md-sys-color-on-surface-variant); border: 1px solid var(--md-sys-color-outline-variant);">${this.escapeHtml(spectralOutput)}</div>`;
  }

  escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  updateProps(newProps) {
    const oldSelectedTab = this.props.selectedTab;
    const oldSpectralOutput = this.props.spectralOutput;
    const oldLogs = this.props.logger?.logs || [];
    
    this.props = { ...this.props, ...newProps };
    
    // Si solo cambió el tab seleccionado, actualizar programáticamente sin disparar eventos
    if (newProps.selectedTab !== undefined && newProps.selectedTab !== oldSelectedTab) {
      this.isUpdating = true;
      const tabs = this.container.querySelector('md-tabs');
      if (tabs && tabs.activeTabIndex !== newProps.selectedTab) {
        tabs.activeTabIndex = newProps.selectedTab;
      }
      // Actualizar solo el contenido sin re-renderizar todo
      const tabContent = this.container.querySelector('.tab-content');
      if (tabContent) {
        const logs = this.props.logger?.logs || [];
        tabContent.innerHTML = newProps.selectedTab === 0 
          ? this.renderAppValidations(logs) 
          : this.renderSpectralValidations(this.props.spectralOutput);
      }
      // Permitir eventos después de un breve delay
      setTimeout(() => {
        this.isUpdating = false;
      }, 100);
    } else if (
      (newProps.spectralOutput !== undefined && newProps.spectralOutput !== oldSpectralOutput) ||
      (newProps.logger && JSON.stringify(newProps.logger.logs) !== JSON.stringify(oldLogs)) ||
      (newProps.language !== undefined && newProps.language !== this.props.language)
    ) {
      // Si cambió el contenido pero no el tab, actualizar solo el contenido visible
      const tabContent = this.container.querySelector('.tab-content');
      const tabsEl = this.container.querySelector('md-tabs');
      if (tabContent) {
        const currentTab = tabsEl ? tabsEl.activeTabIndex : this.props.selectedTab;
        const logs = this.props.logger?.logs || [];
        tabContent.innerHTML = currentTab === 0 
          ? this.renderAppValidations(logs) 
          : this.renderSpectralValidations(this.props.spectralOutput);
      }
    } else {
      // Si cambió algo más, hacer render completo
      this.render();
    }
  }
}

