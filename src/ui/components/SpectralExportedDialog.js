import { Strings } from '../../i18n/strings.js';

export class SpectralExportedDialog {
  constructor(container, props) {
    this.container = container;
    this.props = props;
    this.listeners = [];
    this.render();
  }

  cleanup() {
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
    this.cleanup();
    
    const { show, onDismiss, language } = this.props;

    if (!show) {
      this.container.style.display = 'none';
      return;
    }

    this.container.style.display = 'flex';
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    this.container.setAttribute('data-theme', currentTheme);
    this.container.innerHTML = `
      <md-dialog id="spectral-exported-dialog" open style="margin: auto; position: relative;">
        <div slot="headline">
          <span class="material-symbols-outlined" style="vertical-align: middle; margin-right: 8px;">check_circle</span>
          ${Strings.get(language, 'export.spectral')}
        </div>
        <div slot="content">
          <p style="margin: 0; color: var(--md-sys-color-on-surface-variant);">
            ${Strings.get(language, 'spectral.export.success')}
          </p>
        </div>
        <div slot="actions">
          <md-filled-button id="close-btn">${Strings.get(language, 'ok')}</md-filled-button>
        </div>
      </md-dialog>
    `;

    const dialog = this.container.querySelector('md-dialog');
    if (!dialog) return;
    
    const closeBtn = this.container.querySelector('#close-btn');
    if (closeBtn) {
      this.addEventListener(closeBtn, 'click', () => {
        dialog.close();
        onDismiss();
      });
    }
    
    this.addEventListener(dialog, 'close', () => {
      onDismiss();
    });
    
    // Close on overlay click
    this.addEventListener(this.container, 'click', (e) => {
      if (e.target === this.container) {
        dialog.close();
      }
    });
  }

  updateProps(newProps) {
    this.props = { ...this.props, ...newProps };
    this.render();
  }
}

