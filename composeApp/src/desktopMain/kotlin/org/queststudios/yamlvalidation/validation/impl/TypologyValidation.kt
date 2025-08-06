package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class TypologyValidation : ValidationRule {
    override val name: String = "TypologyValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        val language = org.queststudios.yamlvalidation.core.AppConfig.language
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val typologyBlock = methodObj["x-typology"] as? Map<*, *> ?: return
            val typology = typologyBlock["typology"]?.toString() ?: ""
            if (typology != "external") {
                logger.log("ERROR", Strings.get(language, "typology.error.not_external").replace("{0}", typology))
            } else {
                logger.log("SUCCESS", Strings.get(language, "typology.success.external"))
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarTypology: ${e.message}")
        }
    }
}
