import { Strings } from '../../i18n/strings.js';

export class HelpDialog {
  constructor(container, props) {
    this.container = container;
    this.props = props;
    this.listeners = [];
    this.isClosing = false; // Flag para prevenir múltiples cierres
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
    
    const { show, language, onDismiss } = this.props;
    
    if (!show) {
      this.container.style.display = 'none';
      const existingDialog = this.container.querySelector('md-dialog');
      if (existingDialog) {
        if (existingDialog.open) {
          existingDialog.close();
        }
        existingDialog.removeAttribute('open');
      }
      this.container.style.pointerEvents = 'none';
      return;
    }

    this.container.style.display = 'flex';
    this.container.style.pointerEvents = 'auto';

    const helpContent = Strings.getHelpContent(language);

    this.container.innerHTML = `
      <md-dialog id="help-dialog" data-theme="${document.documentElement.getAttribute('data-theme') || 'light'}">
        <div slot="headline" style="display: flex; align-items: center; gap: 12px;">
          <span class="material-symbols-outlined">help</span>
          ${helpContent.title}
        </div>
        <div slot="content" style="max-height: 70vh; overflow-y: auto; overflow-x: hidden; padding: 8px;">
          ${helpContent.sections.map(section => `
            <div style="margin-bottom: 24px;">
              <h3 style="margin: 0 0 12px 0; color: var(--md-sys-color-primary); display: flex; align-items: center; gap: 8px;">
                <span class="material-symbols-outlined" style="font-size: 20px;">${section.icon}</span>
                ${section.title}
              </h3>
              <div style="color: var(--md-sys-color-on-surface); line-height: 1.6;">
                ${section.content.map(paragraph => `
                  <p style="margin: 0 0 12px 0;">${paragraph}</p>
                `).join('')}
                ${section.list ? `
                  <ul style="margin: 12px 0; padding-left: 24px;">
                    ${section.list.map(item => `<li style="margin-bottom: 8px;">${item}</li>`).join('')}
                  </ul>
                ` : ''}
              </div>
            </div>
          `).join('')}
        </div>
        <div slot="actions">
          <md-text-button id="help-close-btn">${Strings.get(language, 'ok')}</md-text-button>
        </div>
      </md-dialog>
    `;

    const dialog = this.container.querySelector('#help-dialog');
    if (dialog) {
      dialog.show();
    }

    const closeHandler = () => {
      // Prevenir múltiples cierres simultáneos
      if (this.isClosing) {
        return;
      }
      this.isClosing = true;
      
      const currentDialog = this.container.querySelector('#help-dialog');
      if (currentDialog) {
        // Cerrar el diálogo de Material
        if (currentDialog.open) {
          currentDialog.close();
        }
        // Remover el atributo open
        currentDialog.removeAttribute('open');
        // Esperar un momento para que Material Web Components limpie el overlay
        setTimeout(() => {
          // Ocultar el contenedor
          this.container.style.display = 'none';
          // Desactivar pointer-events
          this.container.style.pointerEvents = 'none';
          // Forzar la limpieza del overlay removiendo cualquier elemento residual
          const overlay = document.querySelector('md-dialog[open]');
          if (overlay && overlay === currentDialog) {
            overlay.removeAttribute('open');
            overlay.close();
          }
          // Llamar a onDismiss para actualizar el estado
          if (onDismiss) {
            onDismiss();
          }
          // Resetear el flag después de un delay
          setTimeout(() => {
            this.isClosing = false;
          }, 200);
        }, 150);
      } else {
        // Si no hay diálogo, simplemente ocultar y desactivar pointer-events
        this.container.style.display = 'none';
        this.container.style.pointerEvents = 'none';
        if (onDismiss) {
          setTimeout(() => {
            onDismiss();
            this.isClosing = false;
          }, 100);
        } else {
          this.isClosing = false;
        }
      }
    };

    const closeBtn = this.container.querySelector('#help-close-btn');
    if (closeBtn) {
      this.addEventListener(closeBtn, 'click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        closeHandler();
      });
    }

    // Cerrar con ESC
    const escapeHandler = (e) => {
      if (e.key === 'Escape' && dialog && dialog.open) {
        e.preventDefault();
        e.stopPropagation();
        closeHandler();
      }
    };
    this.addEventListener(document, 'keydown', escapeHandler);

    // Cerrar al hacer clic fuera del diálogo (en el overlay)
    const overlayHandler = (e) => {
      // Solo cerrar si se hace clic directamente en el contenedor (overlay), no en el diálogo
      if (e.target === this.container && dialog && dialog.open) {
        e.preventDefault();
        e.stopPropagation();
        closeHandler();
      }
    };
    this.addEventListener(this.container, 'click', overlayHandler);

    // Prevenir que el clic en el diálogo cierre el diálogo
    if (dialog) {
      this.addEventListener(dialog, 'click', (e) => {
        e.stopPropagation();
      });
      
      // También escuchar el evento 'close' del diálogo para asegurar la limpieza
      // Solo si no estamos ya cerrando manualmente
      this.addEventListener(dialog, 'close', () => {
        if (!this.isClosing) {
          setTimeout(() => {
            this.container.style.display = 'none';
            this.container.style.pointerEvents = 'none';
            if (onDismiss) {
              onDismiss();
            }
          }, 100);
        }
      });
    }
  }

  show() {
    this.props.show = true;
    this.render();
  }

  hide() {
    this.props.show = false;
    this.render();
  }
}

