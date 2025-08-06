package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class AbsisOperationValidation : ValidationRule {
    override val name: String = "AbsisOperationValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        val language = org.queststudios.yamlvalidation.core.AppConfig.language
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
                        logger.log("SUCCESS", Strings.get(language, "absisop.success.get_informational"))
                    } else {
                        logger.log("ERROR", Strings.get(language, "absisop.error.get_type").replace("{0}", type))
                    }
                }
                "post" -> {
                    if (endpoint.endsWith("/request")) {
                        if (type == "informational") {
                            logger.log("SUCCESS", Strings.get(language, "absisop.success.post_request_informational"))
                        } else {
                            logger.log("ERROR", Strings.get(language, "absisop.error.post_request_type").replace("{0}", type))
                        }
                    } else {
                        if (type == "management") {
                            logger.log("SUCCESS", Strings.get(language, "absisop.success.post_management"))
                        } else {
                            logger.log("ERROR", Strings.get(language, "absisop.error.post_type").replace("{0}", type))
                        }
                    }
                }
                "put" -> {
                    if (type == "management") {
                        logger.log("SUCCESS", Strings.get(language, "absisop.success.put_management"))
                    } else {
                        logger.log("ERROR", Strings.get(language, "absisop.error.put_type").replace("{0}", type))
                    }
                }
            }
            if (security.isNotEmpty()) {
                val expected = "$title.$operationId"
                if (security == expected) {
                    logger.log("SUCCESS", Strings.get(language, "absisop.success.security_format"))
                } else {
                    logger.log("ERROR", Strings.get(language, "absisop.error.security_format").replace("{0}", security).replace("{1}", expected))
                }
            } else {
                logger.log("ERROR", Strings.get(language, "absisop.error.security_undefined"))
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarAbsisOperation: ${e.message}")
        }
    }
}
