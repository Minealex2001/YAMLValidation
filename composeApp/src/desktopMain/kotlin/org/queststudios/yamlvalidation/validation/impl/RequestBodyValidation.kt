package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class RequestBodyValidation : ValidationRule {
    override val name: String = "RequestBodyValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val requestBody = methodObj["requestBody"]
            if (method.equals("post", ignoreCase = true) || method.equals("put", ignoreCase = true)) {
                if (requestBody == null) {
                    logger.log("ERROR", "requestBody no está definido para el método '$method'.")
                } else {
                    logger.log("INFO", "Validación exitosa: requestBody está definido para el método '$method' en el endpoint '$endpoint'.")
                }
            } else {
                if (requestBody != null) {
                    logger.log("ERROR", "requestBody está definido para el método '$method', pero solo debería estar presente para métodos POST o PUT.")
                } else {
                    logger.log("INFO", "Validación exitosa: requestBody no está definido para el método '$method' en el endpoint '$endpoint'.")
                }
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarRequestBody: ${e.message}")
        }
    }
}
