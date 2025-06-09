package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class OperationIdValidation : ValidationRule {
    override val name: String = "OperationIdValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val operationId = methodObj["operationId"]?.toString() ?: ""
            if (!endpoint.contains("/int")) {
                if (operationId.startsWith("internal")) {
                    logger.log("ERROR", "operationId no debe llevar prefijo 'internal' para endpoints que no contienen '/int'")
                }
            }
            if (operationId.isEmpty()) {
                logger.log("WARNING", "operationId no está definido para endpoint.")
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarOperationId: ${e.message}")
        }
    }
}
