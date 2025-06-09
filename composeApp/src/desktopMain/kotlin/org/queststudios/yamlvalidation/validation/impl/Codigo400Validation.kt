package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class Codigo400Validation : ValidationRule {
    override val name: String = "Codigo400Validation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val responses = methodObj["responses"] as? Map<*, *> ?: return
            val resp400 = responses["400"] as? Map<*, *>
            val description = resp400?.get("description")?.toString() ?: ""
            val info = yamlData["info"] as? Map<*, *>
            val xFuncDomains = info?.get("x-functional-domains") as? Map<*, *>
            val domain = xFuncDomains?.get("domain")?.toString()?.uppercase() ?: ""
            if (description.isNotEmpty()) {
                if (description.contains("$domain/")) {
                    logger.log("INFO", "Validación exitosa: La descripción del código '400' contiene el dominio en mayúsculas: $domain.")
                } else {
                    logger.log("ERROR", "La descripción del código '400' no contiene el dominio en mayúsculas: $domain. Descripción: $description")
                }
            } else {
                logger.log("WARNING", "La descripción del código '400' no está definida para el endpoint '$endpoint', método '$method'.")
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarCodigo400: ${e.message}")
        }
    }
}
