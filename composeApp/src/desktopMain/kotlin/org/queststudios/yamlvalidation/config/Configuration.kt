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
    private val dontShowSpectralDialogKey = "dontShowSpectralDialog"

    var spectralPath: String = ""
        private set
    var language: String = "es"
        private set
    var dontShowSpectralDialog: Boolean = false
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
                dontShowSpectralDialog = props.getProperty(dontShowSpectralDialogKey, "false").toBoolean()
            } else {
                spectralPath = ""
                language = "es"
                dontShowSpectralDialog = false
            }
        } catch (_: Exception) {
            spectralPath = ""
            language = "es"
            dontShowSpectralDialog = false
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

    fun setDontShowSpectralDialog(value: Boolean) {
        dontShowSpectralDialog = value
        saveConfig()
    }

    private fun saveConfig() {
        try {
            val props = Properties()
            props.setProperty(spectralPathKey, spectralPath)
            props.setProperty(languageKey, language)
            props.setProperty(dontShowSpectralDialogKey, dontShowSpectralDialog.toString())
            val dir = File(configDir)
            if (!dir.exists()) dir.mkdirs()
            FileOutputStream(configFile).use { fos -> props.store(fos, "Configuración Validador YAML") }
        } catch (_: Exception) {}
    }
}
