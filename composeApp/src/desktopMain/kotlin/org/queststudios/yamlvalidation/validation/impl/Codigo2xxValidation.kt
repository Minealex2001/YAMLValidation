package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class Codigo2xxValidation : ValidationRule {
    override val name: String = "Codigo2xxValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        try {
            val codes = listOf("200", "201", "202", "204")
            val paths = context.yamlData["paths"] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val endpointObj = paths[endpoint] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val methodObj = endpointObj[method] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val responses = methodObj["responses"] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val found2xx = codes.any { responses[it] != null }
            if (found2xx) {
                logger.log("INFO", "Validación exitosa: El endpoint '$endpoint' con método '$method' tiene al menos un código de respuesta 2xx.")
            } else {
                logger.log("ERROR", "El endpoint '$endpoint' con método '$method' no tiene ningún código de respuesta 2xx (200, 201, 202, 204).")
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarCodigo2xx: ${e.message}")
        }
    }
}
