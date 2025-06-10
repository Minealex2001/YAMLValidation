package org.queststudios.yamlvalidation.validation.impl

import org.queststudios.yamlvalidation.core.ValidationContext
import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule

class CertificationValidation : ValidationRule {
    override val name: String = "CertificationValidation"

    override fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger) {
        try {
            val yamlData = context.yamlData
            val paths = yamlData["paths"] as? Map<*, *> ?: return
            val endpointObj = paths[endpoint] as? Map<*, *> ?: return
            val methodObj = endpointObj[method] as? Map<*, *> ?: return
            val certBlock = methodObj["x-certification"] as? Map<*, *> ?: return
            val certification = certBlock["certification"]?.toString() ?: ""
            val objective = certBlock["objective"]?.toString() ?: ""
            val year = certBlock["year"]?.toString() ?: ""
            if (certification.isNotEmpty() && certification == "A" || certification == "B" || certification == "C") {
                logger.log("WARNING", "x-certification.certification tiene el valor 'A'. Debería estar vacío si es la primera vez que se presenta a API Team CXB.")
            }
            if (objective != "A") {
                logger.log("INFO", "x-certification.objective debería ser 'A' la primera vez que se presenta. Valor actual: $objective")
            } else {
                logger.log("INFO", "Validación exitosa: x-certification.objective es '$objective'.")
            }
            if (year != "2025") {
                logger.log("ERROR", "x-certification.year no es '2025': $year")
            } else {
                logger.log("INFO", "Validación exitosa: x-certification.year es '2025'.")
            }
        } catch (e: Exception) {
            logger.log("ERROR", "Error en validarCertification: ${e.message}")
        }
    }
}
