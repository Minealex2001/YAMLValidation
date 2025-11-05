import { ValidationRule } from '../ValidationRule.js';
import { Strings } from '../../i18n/strings.js';
import { AppConfig } from '../../core/AppConfig.js';

export class CertificationValidation extends ValidationRule {
  validate(endpoint, method, context, logger) {
    const language = AppConfig.language;
    try {
      const yamlData = context.yamlData;
      const paths = yamlData.paths || {};
      const endpointObj = paths[endpoint];
      if (!endpointObj) return;
      
      const methodObj = endpointObj[method];
      if (!methodObj) return;
      
      const certBlock = methodObj['x-certification'];
      if (!certBlock) return;
      
      const certification = certBlock.certification || '';
      const objective = certBlock.objective || '';
      const year = certBlock.year || '';
      
      if (certification !== '' && (certification === 'A' || certification === 'B' || certification === 'C')) {
        logger.log('WARNING', Strings.get(language, 'certification.warning.not_empty'));
      }
      
      if (objective !== 'A') {
        logger.log('INFO', Strings.get(language, 'certification.info.objective_not_a', objective));
      } else {
        logger.log('SUCCESS', Strings.get(language, 'certification.success.objective_a', objective));
      }
      
      if (/^\d{4}$/.test(year)) {
        logger.log('SUCCESS', Strings.get(language, 'certification.success.year_valid', year));
      } else {
        logger.log('ERROR', Strings.get(language, 'certification.error.year_invalid', year));
      }
    } catch (error) {
      logger.log('ERROR', `Error en validarCertification: ${error.message}`);
    }
  }
}

