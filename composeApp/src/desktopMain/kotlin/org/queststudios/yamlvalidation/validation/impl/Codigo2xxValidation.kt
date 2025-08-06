package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class Codigo2xxValidation : ValidationRule {
    override val name: String = "Codigo2xxValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        val language = org.queststudios.yamlvalidation.core.AppConfig.language
        try {
            val codes = listOf("200", "201", "202", "204")
            val paths = context.yamlData["paths"] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val endpointObj = paths[endpoint] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val methodObj = endpointObj[method] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val responses = methodObj["responses"] as? Map<*, *> ?: emptyMap<Any?, Any?>()
            val found2xx = codes.any { responses[it] != null }
            if (found2xx) {
                logger.log("SUCCESS", Strings.get(language, "codigo2xx.success.found").replace("{0}", endpoint).replace("{1}", method))
            } else {
                logger.log("ERROR", Strings.get(language, "codigo2xx.error.not_found").replace("{0}", endpoint).replace("{1}", method))
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarCodigo2xx: ${e.message}")
        }
    }
}
