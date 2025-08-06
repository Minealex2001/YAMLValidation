package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class CertificationValidation : ValidationRule {
    override val name: String = "CertificationValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        val language = org.queststudios.yamlvalidation.core.AppConfig.language
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val certBlock = methodObj["x-certification"] as? Map<*, *> ?: return
            val certification = certBlock["certification"]?.toString() ?: ""
            val objective = certBlock["objective"]?.toString() ?: ""
            val year = certBlock["year"]?.toString() ?: ""
            if (certification.isNotEmpty() && certification == "A" || certification == "B" || certification == "C") {
                logger.log("WARNING", Strings.get(language, "certification.warning.not_empty"))
            }
            if (objective != "A") {
                logger.log("INFO", Strings.get(language, "certification.info.objective_not_a").replace("{0}", objective))
            } else {
                logger.log("SUCCESS", Strings.get(language, "certification.success.objective_a").replace("{0}", objective))
            }
            // Validar que el año tenga exactamente 4 dígitos
            if (year.matches(Regex("^\\d{4}$"))) {
                logger.log("SUCCESS", Strings.get(language, "certification.success.year_valid").replace("{0}", year))
            } else {
                logger.log("ERROR", Strings.get(language, "certification.error.year_invalid").replace("{0}", year))
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarCertification: ${e.message}")
        }
    }
}
