import { ValidationRule } from '../ValidationRule.js';
import { Strings } from '../../i18n/strings.js';
import { AppConfig } from '../../core/AppConfig.js';

export class AbsisOperationValidation extends ValidationRule {
  validate(endpoint, method, context, logger) {
    const language = AppConfig.language;
    try {
      const yamlData = context.yamlData;
      const paths = yamlData.paths || {};
      const endpointObj = paths[endpoint];
      if (!endpointObj) return;
      
      const methodObj = endpointObj[method];
      if (!methodObj) return;
      
      const absisOp = methodObj['x-absis-operation'];
      if (!absisOp) return;
      
      const type = absisOp.type || '';
      const security = absisOp.security || '';
      const info = yamlData.info || {};
      const title = info.title || '';
      const operationId = methodObj.operationId || '';
      
      const methodLower = method.toLowerCase();
      
      if (methodLower === 'get') {
        if (type === 'informational') {
          logger.log('SUCCESS', Strings.get(language, 'absisop.success.get_informational'));
        } else {
          logger.log('ERROR', Strings.get(language, 'absisop.error.get_type', type));
        }
      } else if (methodLower === 'post') {
        if (endpoint.endsWith('/request')) {
          if (type === 'informational') {
            logger.log('SUCCESS', Strings.get(language, 'absisop.success.post_request_informational'));
          } else {
            logger.log('ERROR', Strings.get(language, 'absisop.error.post_request_type', type));
          }
        } else {
          if (type === 'management') {
            logger.log('SUCCESS', Strings.get(language, 'absisop.success.post_management'));
          } else {
            logger.log('ERROR', Strings.get(language, 'absisop.error.post_type', type));
          }
        }
      } else if (methodLower === 'put') {
        if (type === 'management') {
          logger.log('SUCCESS', Strings.get(language, 'absisop.success.put_management'));
        } else {
          logger.log('ERROR', Strings.get(language, 'absisop.error.put_type', type));
        }
      }
      
      if (security !== '') {
        const expected = `${title}.${operationId}`;
        if (security === expected) {
          logger.log('SUCCESS', Strings.get(language, 'absisop.success.security_format'));
        } else {
          logger.log('ERROR', Strings.get(language, 'absisop.error.security_format', security, expected));
        }
      } else {
        logger.log('ERROR', Strings.get(language, 'absisop.error.security_undefined'));
      }
    } catch (error) {
      logger.log('ERROR', `Error en validarAbsisOperation: ${error.message}`);
    }
  }
}

