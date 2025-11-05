import { Strings } from '../../i18n/strings.js';

export class SpectralDialog {
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
    
    const { show, onDismiss, onSetPath, onSkip, onDontShow, language } = this.props;

    if (!show) {
      this.container.style.display = 'none';
      return;
    }

    this.container.style.display = 'flex';
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    this.container.setAttribute('data-theme', currentTheme);
    this.container.innerHTML = `
      <md-dialog id="spectral-dialog" open style="margin: auto; position: relative;">
        <div slot="headline">
          <span class="material-symbols-outlined" style="vertical-align: middle; margin-right: 8px;">auto_awesome</span>
          ${Strings.get(language, 'export.spectral')}
        </div>
        <div slot="content">
          <p style="margin: 0; color: var(--md-sys-color-on-surface-variant);">
            ${Strings.get(language, 'spectral.askPath')}
          </p>
        </div>
        <div slot="actions">
          <md-text-button id="skip-btn">${Strings.get(language, 'spectral.btnSkip')}</md-text-button>
          <md-text-button id="dont-show-btn">${Strings.get(language, 'spectral.btnDontShow')}</md-text-button>
          <md-filled-button id="set-path-btn">
            <span slot="icon" class="material-symbols-outlined">folder_open</span>
            ${Strings.get(language, 'spectral.btnSetPath')}
          </md-filled-button>
        </div>
      </md-dialog>
    `;

    const dialog = this.container.querySelector('md-dialog');
    if (!dialog) return;
    
    const setPathBtn = this.container.querySelector('#set-path-btn');
    if (setPathBtn) {
      this.addEventListener(setPathBtn, 'click', () => {
        dialog.close();
        onDismiss();
        onSetPath();
      });
    }
    
    const skipBtn = this.container.querySelector('#skip-btn');
    if (skipBtn) {
      this.addEventListener(skipBtn, 'click', () => {
        dialog.close();
        onDismiss();
        onSkip();
      });
    }
    
    const dontShowBtn = this.container.querySelector('#dont-show-btn');
    if (dontShowBtn) {
      this.addEventListener(dontShowBtn, 'click', () => {
        dialog.close();
        onDismiss();
        onDontShow();
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

