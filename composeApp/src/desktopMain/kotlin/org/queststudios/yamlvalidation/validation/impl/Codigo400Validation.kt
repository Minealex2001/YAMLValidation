package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class Codigo400Validation : ValidationRule {
    override val name: String = "Codigo400Validation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        val language = org.queststudios.yamlvalidation.core.AppConfig.language
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
                    logger.log("SUCCESS", Strings.get(language, "codigo400.success.domain_in_description").replace("{0}", domain))
                } else {
                    logger.log("ERROR", Strings.get(language, "codigo400.error.domain_not_in_description").replace("{0}", domain).replace("{1}", description))
                }
            } else {
                logger.log("WARNING", Strings.get(language, "codigo400.warning.description_not_defined").replace("{0}", endpoint).replace("{1}", method))
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarCodigo400: ${e.message}")
        }
    }
}
