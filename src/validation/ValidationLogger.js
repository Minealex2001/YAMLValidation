export class ValidationLogger {
  constructor() {
    this.logs = [];
  }

  log(level, message) {
    this.logs.push({ level, message, timestamp: new Date() });
  }

  clear() {
    this.logs = [];
  }
}

