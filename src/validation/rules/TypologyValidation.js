import { ValidationRule } from '../ValidationRule.js';
import { Strings } from '../../i18n/strings.js';
import { AppConfig } from '../../core/AppConfig.js';

export class TypologyValidation extends ValidationRule {
  validate(endpoint, method, context, logger) {
    const language = AppConfig.language;
    try {
      const yamlData = context.yamlData;
      const paths = yamlData.paths || {};
      const endpointObj = paths[endpoint];
      if (!endpointObj) return;
      
      const methodObj = endpointObj[method];
      if (!methodObj) return;
      
      const typologyBlock = methodObj['x-typology'];
      if (!typologyBlock) return;
      
      const typology = typologyBlock.typology || '';
      
      if (typology !== 'external') {
        logger.log('ERROR', Strings.get(language, 'typology.error.not_external', typology));
      } else {
        logger.log('SUCCESS', Strings.get(language, 'typology.success.external'));
      }
    } catch (error) {
      logger.log('ERROR', `Error en validarTypology: ${error.message}`);
    }
  }
}

