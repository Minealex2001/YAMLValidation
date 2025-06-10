package org.queststudios.yamlvalidation.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.queststudios.yamlvalidation.licensing.LicenseManager
import org.queststudios.yamlvalidation.config.Configuration
import org.queststudios.yamlvalidation.i18n.Strings
import java.io.File
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigDialog(
    showConfigDialog: Boolean,
    onDismiss: () -> Unit,
    licenseValid: Boolean,
    trialStartDate: String?,
    onShowLicenseDialog: () -> Unit,
    spectralPath: String,
    onSpectralPathChange: (String) -> Unit,
    onShowSpectralChooser: () -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    config: Configuration
) {
    if (!showConfigDialog) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.get(language, "config.title")) },
        text = {
            Column {
                // Estado de licencia
                val isActivated = licenseValid
                val trialStartFile = File(System.getProperty("user.home") + File.separator + "Documentos" + File.separator + "ValidadorYAML" + File.separator + "trial_start.txt")
                val trialStart = remember {
                    if (trialStartFile.exists()) {
                        trialStartFile.readText().trim()
                    } else {
                        null
                    }
                }
                val daysLeft = if (trialStart != null) 7 - ChronoUnit.DAYS.between(
                    LocalDate.parse(trialStart),
                    LocalDate.now()
                ).toInt() else 0 // <-- Cambiado a 0 para que trialActive solo sea true si hay días restantes
                val trialActive = trialStart != null && daysLeft > 0
                val trialUsed = trialStart != null && daysLeft <= 0
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(Strings.get(language, "license.status") + ": ", fontWeight = FontWeight.Bold)
                    when {
                        isActivated -> Text(Strings.get(language, "license.activated"), fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                        trialActive -> Text(Strings.get(language, "license.trialActive").replace("{0}", daysLeft.toString()), fontWeight = FontWeight.Bold, color = Color(0xFF00ADB5))
                        trialUsed -> Text(Strings.get(language, "license.trialUsed"), fontWeight = FontWeight.Bold, color = Color(0xFFFFA000))
                        else -> Text(Strings.get(language, "license.notActivated"), fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                    if (!isActivated && !trialActive) {
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            onShowLicenseDialog()
                            onDismiss()
                        }) { Text(Strings.get(language, "license.have")) }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (trialStart == null) {
                                    val today = LocalDate.now().toString()
                                    LicenseManager.saveTrialStartDate(today)
                                    onDismiss()
                                }
                            },
                            enabled = trialStart == null
                        ) { Text(Strings.get(language, "license.trial")) }
                    }
                }
                if (trialActive) {
                    Spacer(Modifier.height(8.dp))
                    Text(Strings.get(language, "license.trialActive").replace("{0}", daysLeft.toString()), color = Color(0xFF00ADB5), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = spectralPath,
                    onValueChange = onSpectralPathChange,
                    label = { Text(Strings.get(language, "spectral.selectFolder")) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Button(onClick = {
                            // Abrir selector de carpetas y actualizar el campo
                            val chooser = javax.swing.JFileChooser()
                            chooser.dialogTitle = Strings.get(language, "export.selectFolder")
                            chooser.fileSelectionMode = javax.swing.JFileChooser.DIRECTORIES_ONLY
                            val result = chooser.showOpenDialog(null)
                            if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                                onSpectralPathChange(chooser.selectedFile.absolutePath)
                            }
                        }) { Text(Strings.get(language, "export.selectFolder")) }
                    }
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = language,
                    onValueChange = {},
                    label = { Text(Strings.get(language, "menu.language")) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        var expanded by remember { mutableStateOf(false) }
                        Button(onClick = { expanded = true }) { Text(Strings.get(language, "menu.language")) }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text(Strings.get("es", "language.name")) }, onClick = { onLanguageChange("es"); expanded = false; config.setLanguage("es") })
                            DropdownMenuItem(text = { Text(Strings.get("en", "language.name")) }, onClick = { onLanguageChange("en"); expanded = false; config.setLanguage("en") })
                            DropdownMenuItem(text = { Text(Strings.get("ca", "language.name")) }, onClick = { onLanguageChange("ca"); expanded = false; config.setLanguage("ca") })
                        }
                    }
                )
                Spacer(Modifier.height(12.dp))
                // Botón para ver el changelog
                var showChangelog by remember { mutableStateOf(false) }
                if (showChangelog) {
                    val resource = object {}.javaClass.classLoader.getResourceAsStream("changelog.md")
                    val changelogText = resource?.bufferedReader()?.readText() ?: "No se encontró changelog.md"
                    ChangelogDialog(
                        changelog = changelogText,
                        onDismiss = { showChangelog = false }
                    )
                }
                Button(onClick = { showChangelog = true }) {
                    Text(Strings.get(language, "config.changelog"))
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text(Strings.get(language, "config.close")) }
        }
    )
}
