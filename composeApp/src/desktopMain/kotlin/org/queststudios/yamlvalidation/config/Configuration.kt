package org.queststudios.yamlvalidation.config

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class Configuration {
    private val configDir = System.getProperty("user.home") + File.separator + "Documentos" + File.separator + "ValidadorYAML"
    private val configFile = configDir + File.separator + "configuracion.properties"
    private val spectralPathKey = "spectralPath"
    private val languageKey = "language"

    var spectralPath: String = ""
        private set
    var language: String = "es"
        private set

    init {
        loadConfig()
    }

    private fun loadConfig() {
        try {
            val dir = File(configDir)
            if (!dir.exists()) dir.mkdirs()
            val file = File(configFile)
            if (file.exists()) {
                val props = Properties()
                FileInputStream(file).use { fis -> props.load(fis) }
                spectralPath = props.getProperty(spectralPathKey, "")
                language = props.getProperty(languageKey, "es")
            } else {
                spectralPath = ""
                language = "es"
            }
        } catch (_: Exception) {
            spectralPath = ""
            language = "es"
        }
    }

    fun isSpectralPathSet(): Boolean = spectralPath.isNotBlank()

    fun setSpectralPath(path: String) {
        spectralPath = path
        saveConfig()
    }

    fun setLanguage(lang: String) {
        language = lang
        saveConfig()
    }

    private fun saveConfig() {
        try {
            val props = Properties()
            props.setProperty(spectralPathKey, spectralPath)
            props.setProperty(languageKey, language)
            val dir = File(configDir)
            if (!dir.exists()) dir.mkdirs()
            FileOutputStream(configFile).use { fos -> props.store(fos, "Configuración Validador YAML") }
        } catch (_: Exception) {}
    }
}
