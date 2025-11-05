import { ValidationRule } from '../ValidationRule.js';
import { Strings } from '../../i18n/strings.js';
import { AppConfig } from '../../core/AppConfig.js';

export class Codigo400Validation extends ValidationRule {
  validate(endpoint, method, context, logger) {
    const language = AppConfig.language;
    try {
      const yamlData = context.yamlData;
      const paths = yamlData.paths || {};
      const endpointObj = paths[endpoint];
      if (!endpointObj) return;
      
      const methodObj = endpointObj[method];
      if (!methodObj) return;
      
      const responses = methodObj.responses || {};
      const resp400 = responses['400'];
      const description = resp400?.description || '';
      
      const info = yamlData.info || {};
      const xFuncDomains = info['x-functional-domains'] || {};
      const domain = (xFuncDomains.domain || '').toUpperCase();
      
      if (description !== '') {
        if (description.includes(`${domain}/`)) {
          logger.log('SUCCESS', Strings.get(language, 'codigo400.success.domain_in_description', domain));
        } else {
          logger.log('ERROR', Strings.get(language, 'codigo400.error.domain_not_in_description', domain, description));
        }
      } else {
        logger.log('WARNING', Strings.get(language, 'codigo400.warning.description_not_defined', endpoint, method));
      }
    } catch (error) {
      logger.log('ERROR', `Error en validarCodigo400: ${error.message}`);
    }
  }
}

