package org.queststudios.yamlvalidation.licensing

import java.security.MessageDigest
import java.util.Base64
import java.io.File

object LicenseManager {
    private val licenseFile = File(System.getProperty("user.home") + File.separator + "Documentos" + File.separator + "ValidadorYAML" + File.separator + "license.key")
    private const val secret = "VALIDADOR-YAML-2025" // Cambia este valor para tu app

    fun isLicenseValid(): Boolean {
        if (!licenseFile.exists()) return false
        val key = licenseFile.readText().trim()
        return validateKey(key)
    }

    fun saveLicenseKey(key: String) {
        licenseFile.parentFile?.mkdirs()
        licenseFile.writeText(key.trim())
    }

    fun validateKey(key: String): Boolean {
        // La clave válida es: base64(sha256(secret + "-" + yyyy-mm-dd))
        // Por ejemplo, para una clave generada hoy
        val today = java.time.LocalDate.now().toString()
        val validKey = generateKey(today)
        return key == validKey
    }

    fun generateKey(date: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = "$secret-$date"
        val hash = md.digest(input.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }

    // --- Gestión de licencia y prueba encriptada ---
    private const val trialPrefix = "TRIAL:"

    fun saveTrialStartDate(date: String) {
        licenseFile.parentFile?.mkdirs()
        val encoded = Base64.getEncoder().encodeToString((trialPrefix + encrypt(date)).toByteArray())
        licenseFile.writeText(encoded)
    }

    fun getTrialStartDate(): String? {
        if (!licenseFile.exists()) return null
        val content = licenseFile.readText().trim()
        return try {
            val decoded = String(Base64.getDecoder().decode(content))
            if (decoded.startsWith(trialPrefix)) {
                decrypt(decoded.removePrefix(trialPrefix))
            } else null
        } catch (e: Exception) { null }
    }

    // Simple "encriptado" reversible (XOR con secret, luego base64)
    private fun encrypt(input: String): String {
        val key = secret.toByteArray()
        val inputBytes = input.toByteArray()
        val out = ByteArray(inputBytes.size)
        for (i in inputBytes.indices) {
            out[i] = (inputBytes[i].toInt() xor key[i % key.size].toInt()).toByte()
        }
        return Base64.getEncoder().encodeToString(out)
    }
    private fun decrypt(input: String): String {
        val key = secret.toByteArray()
        val inputBytes = Base64.getDecoder().decode(input)
        val out = ByteArray(inputBytes.size)
        for (i in inputBytes.indices) {
            out[i] = (inputBytes[i].toInt() xor key[i % key.size].toInt()).toByte()
        }
        return String(out)
    }
}
