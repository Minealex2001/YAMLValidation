export class ThemeToggle {
  constructor(container) {
    this.container = container;
    this.currentTheme = this.loadTheme();
    this.render();
  }

  loadTheme() {
    try {
      return localStorage.getItem('theme') || 'light';
    } catch (error) {
      return 'light';
    }
  }

  async saveTheme(theme) {
    try {
      localStorage.setItem('theme', theme);
      // También guardar en electron store si está disponible
      if (window.electronAPI) {
        await window.electronAPI.storeSet('app.theme', theme);
      }
    } catch (error) {
      console.error('Error saving theme:', error);
    }
  }

  async toggleTheme() {
    this.currentTheme = this.currentTheme === 'light' ? 'dark' : 'light';
    await this.applyTheme(this.currentTheme);
    await this.saveTheme(this.currentTheme);
    
    // Notificar cambio de tema para actualizar componentes que dependen del tema
    window.dispatchEvent(new CustomEvent('theme-changed', { detail: { theme: this.currentTheme } }));
  }

  async applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    document.body.setAttribute('data-theme', theme);
    
    // Aplicar tema a todos los diálogos y componentes Material
    document.querySelectorAll('.dialog-overlay').forEach(overlay => {
      overlay.setAttribute('data-theme', theme);
    });
    
    // Aplicar tema a componentes Material
    document.querySelectorAll('md-dialog').forEach(dialog => {
      dialog.setAttribute('data-theme', theme);
    });
    
    document.querySelectorAll('.card').forEach(card => {
      card.setAttribute('data-theme', theme);
    });
  }

  async render() {
    await this.applyTheme(this.currentTheme);
    
    this.container.innerHTML = `
      <md-icon-button id="theme-toggle-btn" aria-label="Toggle theme">
        <span class="material-symbols-outlined" id="theme-icon">
          ${this.currentTheme === 'light' ? 'dark_mode' : 'light_mode'}
        </span>
      </md-icon-button>
    `;

    const toggleBtn = this.container.querySelector('#theme-toggle-btn');
    toggleBtn.addEventListener('click', () => {
      this.toggleTheme();
      const icon = this.container.querySelector('#theme-icon');
      icon.textContent = this.currentTheme === 'light' ? 'dark_mode' : 'light_mode';
    });
  }

  async init() {
    // Aplicar tema inicial desde localStorage
    const savedTheme = this.loadTheme();
    if (savedTheme) {
      this.currentTheme = savedTheme;
      await this.applyTheme(this.currentTheme);
    }
    
    // Esperar a que electronAPI esté disponible y cargar tema desde store
    let attempts = 0;
    while (!window.electronAPI && attempts < 50) {
      await new Promise(resolve => setTimeout(resolve, 100));
      attempts++;
    }
    
    if (window.electronAPI) {
      try {
        const storeTheme = await window.electronAPI.storeGet('app.theme');
        if (storeTheme && storeTheme !== this.currentTheme) {
          this.currentTheme = storeTheme;
          await this.applyTheme(this.currentTheme);
          await this.saveTheme(this.currentTheme);
          const icon = this.container.querySelector('#theme-icon');
          if (icon) {
            icon.textContent = this.currentTheme === 'light' ? 'dark_mode' : 'light_mode';
          }
        }
      } catch (error) {
        console.error('Error loading theme from store:', error);
      }
    }
  }
}

