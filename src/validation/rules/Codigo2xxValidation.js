import { ValidationRule } from '../ValidationRule.js';
import { Strings } from '../../i18n/strings.js';
import { AppConfig } from '../../core/AppConfig.js';

export class Codigo2xxValidation extends ValidationRule {
  validate(endpoint, method, context, logger) {
    const language = AppConfig.language;
    try {
      const codes = ['200', '201', '202', '204'];
      const paths = context.yamlData.paths || {};
      const endpointObj = paths[endpoint] || {};
      const methodObj = endpointObj[method] || {};
      const responses = methodObj.responses || {};
      
      const found2xx = codes.some(code => responses[code] != null);
      
      if (found2xx) {
        logger.log('SUCCESS', Strings.get(language, 'codigo2xx.success.found', endpoint, method));
      } else {
        logger.log('ERROR', Strings.get(language, 'codigo2xx.error.not_found', endpoint, method));
      }
    } catch (error) {
      logger.log('ERROR', `Error en validarCodigo2xx: ${error.message}`);
    }
  }
}

