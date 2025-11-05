import { ValidationRule } from '../ValidationRule.js';
import { Strings } from '../../i18n/strings.js';
import { AppConfig } from '../../core/AppConfig.js';

export class RequestBodyValidation extends ValidationRule {
  validate(endpoint, method, context, logger) {
    const language = AppConfig.language;
    try {
      const yamlData = context.yamlData;
      const paths = yamlData.paths || {};
      const endpointObj = paths[endpoint];
      if (!endpointObj) return;
      
      const methodObj = endpointObj[method];
      if (!methodObj) return;
      
      const requestBody = methodObj.requestBody;
      const methodLower = method.toLowerCase();
      
      if (methodLower === 'post' || methodLower === 'put') {
        if (requestBody == null) {
          logger.log('ERROR', Strings.get(language, 'requestbody.error.not_defined', method));
        } else {
          logger.log('SUCCESS', Strings.get(language, 'requestbody.success.defined', method, endpoint));
        }
      } else {
        if (requestBody != null) {
          logger.log('ERROR', Strings.get(language, 'requestbody.error.defined_for_wrong_method', method));
        } else {
          logger.log('SUCCESS', Strings.get(language, 'requestbody.success.not_defined', method, endpoint));
        }
      }
    } catch (error) {
      logger.log('ERROR', `Error en validarRequestBody: ${error.message}`);
    }
  }
}

