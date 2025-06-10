package org.queststudios.yamlvalidation.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    private val language: String = "es", // Ahora configurable
    private val spectralPath: String? = null // NUEVO: ruta para spectral
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
                val errMsg = Strings.get(language, "validation_log_file_write_error") + ": ${e.message}"
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
                val msg = Strings.get(language, "validation_yaml_file_not_found")
                log("ERROR", msg)
                yamlData = emptyMap()
                return ValidationResult(false, msg)
            }
            val yaml = Yaml(SafeConstructor(LoaderOptions()))
            val loaded = input.inputStream().use { yaml.load<Map<String, Any?>>(it) }
            yamlData = loaded ?: emptyMap()
            if (!yamlData.containsKey("paths")) {
                val msg = Strings.get(language, "validation_yaml_missing_paths")
                log("ERROR", msg)
                yamlData = emptyMap()
                return ValidationResult(false, msg)
            }
            // Log de éxito tras carga
            log("SUCCESS", "YAML loaded successfully. Keys: ${yamlData.keys}")
        } catch (e: Exception) {
            val msg = Strings.get(language, "validation_yaml_load_error") + ": ${e.message ?: ""}\n${e.stackTraceToString()}"
            log("ERROR", msg)
            yamlData = emptyMap()
            return ValidationResult(false, msg)
        }
        return ValidationResult(true)
    }

    private fun runSpectralValidation(): ValidationResult {
        try {
            val spectralCmd = listOf(
                "cmd", "/c",
                "spectral lint -r ./poc/.spectral_v2.yaml -f pretty \"$yamlPath\""
            )
            val processBuilder = ProcessBuilder(spectralCmd)
            // Usar la ruta especificada por el usuario
            val spectralDir = spectralPath?.takeIf { it.isNotBlank() }?.let { File(it) } ?: File(System.getProperty("user.dir"))
            processBuilder.directory(spectralDir)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            logger.log("SPECTRAL", output)
            logToFile("SPECTRAL", output)
            return if (exitCode == 0) {
                ValidationResult(true)
            } else {
                ValidationResult(false, "Spectral lint encontró errores.\n$output")
            }
        } catch (e: Exception) {
            val msg = "Error ejecutando Spectral: ${e.message}\n${e.stackTraceToString()}"
            logger.log("ERROR", msg)
            logToFile("ERROR", msg)
            return ValidationResult(false, msg)
        }
    }

    data class ParallelValidationResult(
        val internal: ValidationResult,
        val spectral: String // salida de spectral (si hay error, también aquí)
    )

    suspend fun runAllValidationsParallel(): ParallelValidationResult = coroutineScope {
        val spectralDeferred = async(Dispatchers.IO) { runSpectralValidationOutput() }
        val internalDeferred = async(Dispatchers.Default) { runInternalValidations() }
        ParallelValidationResult(
            internal = internalDeferred.await(),
            spectral = spectralDeferred.await()
        )
    }

    private fun runSpectralValidationOutput(): String {
        try {
            val spectralCmd = listOf(
                "cmd", "/c",
                "spectral lint -r ./poc/.spectral_v2.yaml -f pretty \"$yamlPath\""
            )
            val processBuilder = ProcessBuilder(spectralCmd)
            val spectralDir = spectralPath?.takeIf { it.isNotBlank() }?.let { File(it) } ?: File(System.getProperty("user.dir"))
            processBuilder.directory(spectralDir)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            return output
        } catch (e: Exception) {
            return "Error ejecutando Spectral: ${e.message}\n${e.stackTraceToString()}"
        }
    }

    private fun runInternalValidations(): ValidationResult {
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

    fun runAllValidations(): ValidationResult {

        val spectralResult = runSpectralValidation()
        if (!spectralResult.success) return spectralResult
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

    fun exportSpectralToFile(outputPath: String): ValidationResult {
        try {
            val spectralCmd = listOf(
                "cmd", "/c",
                "spectral lint -r ./poc/.spectral_v2.yaml -f pretty \"$yamlPath\" > \"$outputPath\""
            )
            val spectralDir = spectralPath?.takeIf { it.isNotBlank() }?.let { File(it) } ?: File(System.getProperty("user.dir"))
            val processBuilder = ProcessBuilder(spectralCmd)
            processBuilder.directory(spectralDir)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            logger.log("SPECTRAL", output)
            logToFile("SPECTRAL", output)
            return ValidationResult(true)
        } catch (e: Exception) {
            val msg = "Error ejecutando Spectral export: ${e.message}\n${e.stackTraceToString()}"
            logger.log("ERROR", msg)
            logToFile("ERROR", msg)
            return ValidationResult(false, msg)
        }
    }
}
