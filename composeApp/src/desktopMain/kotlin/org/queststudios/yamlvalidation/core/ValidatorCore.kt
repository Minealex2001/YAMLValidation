package org.queststudios.yamlvalidation.core

import org.queststudios.yamlvalidation.validation.ValidationLogger
import org.queststudios.yamlvalidation.validation.ValidationRule
import java.io.File
import java.io.FileWriter
import java.io.IOException
import org.queststudios.yamlvalidation.i18n.Strings
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.LoaderOptions

data class ValidationResult(val success: Boolean, val errorMessage: String? = null)

class ValidatorCore(
    private val yamlPath: String,
    private val logger: ValidationLogger,
    private val rules: List<ValidationRule>,
    private val logFilePath: String? = null,
    private val language: String = "es" // Ahora configurable
) {
    var yamlData: Map<String, Any?> = emptyMap()
        private set
    private val logHistory = mutableListOf<String>()

    private fun logToFile(level: String, message: String) {
        logFilePath?.let {
            try {
                FileWriter(it, true).use { fw ->
                    fw.write("[$level] $message\n")
                }
            } catch (e: IOException) {
                val errMsg = Strings.get(language, "log_file_write_error") + ": ${e.message}"
                logger.log("ERROR", errMsg)
                logHistory.add("[ERROR] $errMsg")
            }
        }
    }

    private fun log(level: String, message: String) {
        logger.log(level, message)
        logToFile(level, message)
        logHistory.add("[$level] $message")
    }

    fun getLogHistory(): List<String> = logHistory.toList()

    fun loadYaml(): ValidationResult {
        try {
            val input = File(yamlPath)
            if (!input.exists() || !input.isFile) {
                val msg = Strings.get(language, "yaml_file_not_found") + ": $yamlPath"
                log("ERROR", msg)
                yamlData = emptyMap()
                return ValidationResult(false, msg)
            }
            val yaml = Yaml(SafeConstructor(LoaderOptions()))
            val loaded = input.inputStream().use { yaml.load<Map<String, Any?>>(it) }
            yamlData = loaded ?: emptyMap()
            if (!yamlData.containsKey("paths")) {
                val msg = Strings.get(language, "yaml_missing_paths")
                log("ERROR", msg)
                yamlData = emptyMap()
                return ValidationResult(false, msg)
            }
            // Log de éxito tras carga
            log("SUCCESS", "YAML loaded successfully. Keys: ${yamlData.keys}")
        } catch (e: Exception) {
            val msg = Strings.get(language, "yaml_load_error") + ": ${e.message ?: ""}\n${e.stackTraceToString()}"
            log("ERROR", msg)
            yamlData = emptyMap()
            return ValidationResult(false, msg)
        }
        return ValidationResult(true)
    }

    fun runAllValidations(): ValidationResult {
        val loadResult = loadYaml()
        if (!loadResult.success) return loadResult
        val paths = yamlData["paths"] as? Map<*, *> ?: emptyMap<Any?, Any?>()
        for (endpoint in paths.keys) {
            val endpointObj = paths[endpoint] as? Map<*, *> ?: continue
            for (method in endpointObj.keys) {
                val methodObj = endpointObj[method] as? Map<*, *> ?: continue
                val context = ValidationContext(yamlPath, yamlData)
                for (rule in rules) {
                    rule.validate(endpoint.toString(), method.toString(), context, object : ValidationLogger {
                        override fun log(level: String, message: String) {
                            this@ValidatorCore.log(level, message)
                        }
                    })
                }
            }
        }
        return ValidationResult(true)
    }
}
