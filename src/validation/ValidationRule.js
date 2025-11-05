export class ValidationLogger {
  log(level, message) {
    // To be implemented by concrete logger
  }
}

export class ValidationRule {
  get name() {
    return this.constructor.name;
  }

  validate(endpoint, method, context, logger) {
    // To be implemented by concrete rules
    throw new Error('validate() must be implemented by subclass');
  }
}

