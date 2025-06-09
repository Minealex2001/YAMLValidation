package org.queststudios.yamlvalidation.validation

import org.queststudios.yamlvalidation.core.ValidationContext

interface ValidationLogger {
    fun log(level: String, message: String)
}

interface ValidationRule {
    fun validate(endpoint: String, method: String, context: ValidationContext, logger: ValidationLogger)
    val name: String
}
