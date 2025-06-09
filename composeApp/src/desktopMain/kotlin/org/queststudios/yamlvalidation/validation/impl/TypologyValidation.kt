package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class TypologyValidation : ValidationRule {
    override val name: String = "TypologyValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val typologyBlock = methodObj["x-typology"] as? Map<*, *> ?: return
            val typology = typologyBlock["typology"]?.toString() ?: ""
            if (typology != "external") {
                logger.log("ERROR", "x-typology.typology no es 'external': $typology")
            } else {
                logger.log("INFO", "Validación exitosa: x-typology.typology es 'external'.")
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarTypology: ${e.message}")
        }
    }
}
