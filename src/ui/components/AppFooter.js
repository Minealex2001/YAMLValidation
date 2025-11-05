import { LicenseManager } from '../../licensing/LicenseManager.js';

export class AppFooter {
  constructor(container, props = {}) {
    this.container = container;
    this.props = props;
    this.isNTT = false;
    this.render();
  }

  async render() {
    // Verificar si es licencia de NTT
    if (this.props.onLicenseCheck) {
      this.isNTT = await this.props.onLicenseCheck();
    } else {
      this.isNTT = await LicenseManager.isNTTLicense();
    }

    if (this.isNTT) {
      this.container.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: center; gap: 16px; flex-wrap: wrap;">
          <span style="font-size: 18px; font-weight: 700; color: var(--md-sys-color-primary); letter-spacing: 2px; text-transform: uppercase;">
            NTT DATA
          </span>
          <span style="color: var(--md-sys-color-on-surface-variant); font-size: 0.875rem;">•</span>
          <p style="margin: 0; color: var(--md-sys-color-on-surface-variant); font-size: 0.875rem;">
            © 2025 <strong style="font-weight: 600; color: var(--md-sys-color-on-surface);"><a href="https://minealexgames.com" target="_blank" rel="noopener noreferrer" style="color: inherit; text-decoration: none;">Minealex Games</a></strong> - YAML Validator | Licencia cedida a NTT Data
          </p>
        </div>
      `;
    } else {
      this.container.innerHTML = `
        <p style="margin: 0; color: var(--md-sys-color-on-surface-variant); font-size: 0.875rem;">© 2025 <a href="https://minealexgames.com" target="_blank" rel="noopener noreferrer" style="color: inherit; text-decoration: none;">Minealex Games</a> - YAML Validator</p>
      `;
    }
  }

  async update() {
    await this.render();
  }
}

