package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class AbsisOperationValidation : ValidationRule {
    override val name: String = "AbsisOperationValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val absisOp = methodObj["x-absis-operation"] as? Map<*, *> ?: return
            val type = absisOp["type"]?.toString() ?: ""
            val security = absisOp["security"]?.toString() ?: ""
            val info = yamlData["info"] as? Map<*, *>
            val title = info?.get("title")?.toString() ?: ""
            val operationId = methodObj["operationId"]?.toString() ?: ""
            when (method.lowercase()) {
                "get" -> {
                    if (type == "informational") {
                        logger.log("INFO", "Validación exitosa: x-absis-operation.type es 'informational' para método GET.")
                    } else {
                        logger.log("ERROR", "x-absis-operation.type valor actual: $type, valor esperado 'informational'")
                    }
                }
                "post", "put" -> {
                    if (type == "management") {
                        logger.log("INFO", "Validación exitosa: x-absis-operation.type es 'management' para método POST o PUT.")
                    } else {
                        logger.log("ERROR", "x-absis-operation.type valor actual: $type , valor esperado: 'management'")
                    }
                }
            }
            if (security.isNotEmpty()) {
                val expected = "$title.$operationId"
                if (security == expected) {
                    logger.log("INFO", "Validación exitosa: x-absis-operation.security tiene el formato correcto.")
                } else {
                    logger.log("ERROR", "x-absis-operation.security no tiene el formato correcto: $security (se esperaba: $expected)")
                }
            } else {
                logger.log("ERROR", "x-absis-operation.security no está definido.")
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarAbsisOperation: ${e.message}")
        }
    }
}
