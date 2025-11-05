import { Strings } from '../i18n/strings.js';

// Use yaml from window (exposed by preload)
const yaml = window.yaml;

export class ValidationResult {
  constructor(success, errorMessage = null) {
    this.success = success;
    this.errorMessage = errorMessage;
  }
}

export class ValidatorCore {
  constructor(yamlPath, logger, rules, logFilePath = null, language = 'es', spectralPath = null) {
    this.yamlPath = yamlPath;
    this.logger = logger;
    this.rules = rules;
    this.logFilePath = logFilePath;
    this.language = language;
    this.spectralPath = spectralPath;
    this.yamlData = {};
    this.logHistory = [];
  }

  getLogHistory() {
    return [...this.logHistory];
  }

  async loadYaml() {
    if (!this.yamlPath || this.yamlPath.trim() === '') {
      const msg = Strings.get(this.language, 'error.noYaml');
      this.log('ERROR', msg);
      this.yamlData = {};
      return new ValidationResult(false, msg);
    }

    try {
      const result = await window.electronAPI.readFile(this.yamlPath);
      if (!result.success) {
        const msg = Strings.get(this.language, 'validation_yaml_file_not_found');
        this.log('ERROR', msg);
        this.yamlData = {};
        return new ValidationResult(false, msg);
      }

      this.yamlData = yaml.load(result.content);
      
      if (!this.yamlData || !this.yamlData.paths) {
        const msg = Strings.get(this.language, 'validation_yaml_missing_paths');
        this.log('ERROR', msg);
        this.yamlData = {};
        return new ValidationResult(false, msg);
      }

      this.log('SUCCESS', `YAML loaded successfully. Keys: ${Object.keys(this.yamlData).join(', ')}`);
      return new ValidationResult(true);
    } catch (error) {
      const msg = `${Strings.get(this.language, 'validation_yaml_load_error')}: ${error.message}`;
      this.log('ERROR', msg);
      this.yamlData = {};
      return new ValidationResult(false, msg);
    }
  }

  async runAllValidationsParallel() {
    // Solo ejecutar Spectral si hay spectralPath (solo para licencias de NTT)
    if (this.spectralPath) {
      const [spectralResult, internalResult] = await Promise.all([
        this.runSpectralValidationOutput(),
        this.runInternalValidations()
      ]);

      return {
        internal: internalResult,
        spectral: spectralResult
      };
    } else {
      // Si no hay spectralPath, solo ejecutar validaciones internas
      const internalResult = await this.runInternalValidations();
      return {
        internal: internalResult,
        spectral: ''
      };
    }
  }

  async runSpectralValidationOutput() {
    try {
      // Read YAML file
      const result = await window.electronAPI.readFile(this.yamlPath);
      if (!result.success) {
        return 'No se pudo leer el YAML.';
      }

      const yamlData = yaml.load(result.content);
      
      // Modify title temporarily
      if (yamlData.info) {
        yamlData.info.title = 'TemporalSpectralName';
      }

      // Create temporary file
      const tempYaml = yaml.dump(yamlData);
      const tempPath = `${this.yamlPath}.spectral_temp.yaml`;
      
      await window.electronAPI.writeFile(tempPath, tempYaml);

      // Run Spectral
      const spectralResult = await window.electronAPI.runSpectral({
        spectralPath: this.spectralPath,
        yamlPath: tempPath,
        spectralDir: this.spectralPath || undefined
      });

      // Note: Temp file cleanup would need to be handled by the main process
      // For now, we'll leave it for manual cleanup or implement a cleanup handler later

      return spectralResult.success ? spectralResult.output : `Error ejecutando Spectral: ${spectralResult.error}`;
    } catch (error) {
      return `Error ejecutando Spectral: ${error.message}`;
    }
  }

  async runInternalValidations() {
    const loadResult = await this.loadYaml();
    if (!loadResult.success) {
      return loadResult;
    }

    const paths = this.yamlData.paths || {};
    
    for (const [endpoint, endpointObj] of Object.entries(paths)) {
      if (!endpointObj || typeof endpointObj !== 'object') continue;
      
      for (const [method, methodObj] of Object.entries(endpointObj)) {
        if (!methodObj || typeof methodObj !== 'object') continue;
        
        const context = new ValidationContext(this.yamlData);
        
        for (const rule of this.rules) {
          rule.validate(endpoint, method, context, {
            log: (level, message) => this.log(level, message)
          });
        }
      }
    }

    return new ValidationResult(true);
  }

  async exportSpectralToFile(outputPath) {
    try {
      const result = await window.electronAPI.runSpectral({
        spectralPath: this.spectralPath,
        yamlPath: this.yamlPath,
        spectralDir: this.spectralPath || undefined
      });

      if (result.success) {
        await window.electronAPI.writeFile(outputPath, result.output);
        this.log('SPECTRAL', result.output);
        return new ValidationResult(true);
      } else {
        const msg = `Error ejecutando Spectral export: ${result.error}`;
        this.log('ERROR', msg);
        return new ValidationResult(false, msg);
      }
    } catch (error) {
      const msg = `Error ejecutando Spectral export: ${error.message}`;
      this.log('ERROR', msg);
      return new ValidationResult(false, msg);
    }
  }

  log(level, message) {
    this.logger.log(level, message);
    this.logHistory.push(`[${level}] ${message}`);
    
    if (this.logFilePath) {
      // Note: File logging would need to be done via IPC in Electron
      // For now, we just log to console
      console.log(`[${level}] ${message}`);
    }
  }
}

export class ValidationContext {
  constructor(yamlData) {
    this.yamlData = yamlData;
  }
}

