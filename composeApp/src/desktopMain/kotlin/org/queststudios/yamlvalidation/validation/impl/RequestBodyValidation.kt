package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class RequestBodyValidation : ValidationRule {
    override val name: String = "RequestBodyValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        val language = org.queststudios.yamlvalidation.core.AppConfig.language // Usar idioma global de la app
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val requestBody = methodObj["requestBody"]
            if (method.equals("post", ignoreCase = true) || method.equals("put", ignoreCase = true)) {
                if (requestBody == null) {
                    logger.log("ERROR", Strings.get(language, "requestbody.error.not_defined").replace("{0}", method))
                } else {
                    logger.log("SUCCESS", Strings.get(language, "requestbody.success.defined").replace("{0}", method).replace("{1}", endpoint))
                }
            } else {
                if (requestBody != null) {
                    logger.log("ERROR", Strings.get(language, "requestbody.error.defined_for_wrong_method").replace("{0}", method))
                } else {
                    logger.log("SUCCESS", Strings.get(language, "requestbody.success.not_defined").replace("{0}", method).replace("{1}", endpoint))
                }
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarRequestBody: ${e.message}")
        }
    }
}
