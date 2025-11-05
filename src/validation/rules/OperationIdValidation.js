import { ValidationRule } from '../ValidationRule.js';
import { Strings } from '../../i18n/strings.js';
import { AppConfig } from '../../core/AppConfig.js';

export class OperationIdValidation extends ValidationRule {
  validate(endpoint, method, context, logger) {
    const language = AppConfig.language;
    try {
      const yamlData = context.yamlData;
      const paths = yamlData.paths || {};
      const endpointObj = paths[endpoint];
      if (!endpointObj) return;
      
      const methodObj = endpointObj[method];
      if (!methodObj) return;
      
      const operationId = methodObj.operationId || '';
      
      if (!endpoint.includes('/int')) {
        if (operationId.startsWith('internal')) {
          logger.log('ERROR', Strings.get(language, 'operationid.error.internal_prefix'));
        }
      }
      
      if (operationId === '') {
        logger.log('WARNING', Strings.get(language, 'operationid.warning.not_defined'));
      }
    } catch (error) {
      logger.log('ERROR', `Error en validarOperationId: ${error.message}`);
    }
  }
}

