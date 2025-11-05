export class ErrorBanner {
  constructor(container) {
    this.container = container;
    this.hide();
  }

  show(message) {
    this.container.innerHTML = `
      <span class="material-symbols-outlined" style="vertical-align: middle; margin-right: 8px;">error</span>
      <span style="flex: 1;">${this.escapeHtml(message)}</span>
      <md-icon-button id="close-banner-btn" style="margin-left: auto;">
        <span class="material-symbols-outlined">close</span>
      </md-icon-button>
    `;
    this.container.classList.remove('hidden');
    
    // Close button event listener
    const closeBtn = this.container.querySelector('#close-banner-btn');
    if (closeBtn) {
      closeBtn.addEventListener('click', () => this.hide());
    }
  }

  escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  hide() {
    this.container.classList.add('hidden');
  }
}

