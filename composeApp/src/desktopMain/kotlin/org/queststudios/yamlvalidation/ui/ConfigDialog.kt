package org.queststudios.yamlvalidation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.queststudios.yamlvalidation.licensing.LicenseManager
import org.queststudios.yamlvalidation.config.Configuration
import org.queststudios.yamlvalidation.i18n.Strings
import java.io.File

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
                val daysLeft = if (trialStart != null) 7 - java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.parse(trialStart),
                    java.time.LocalDate.now()
                ).toInt() else 7
                val trialActive = !isActivated && trialStart != null && daysLeft > 0
                val trialUsed = trialStartDate != null
                val licenseStatus = when {
                    isActivated -> Strings.get(language, "license.activated")
                    trialActive -> Strings.get(language, "license.trialActive").replace("{0}", daysLeft.toString())
                    else -> Strings.get(language, "license.notActivated")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(Strings.get(language, "license.status") + ": $licenseStatus", fontWeight = FontWeight.Bold)
                    if (!isActivated) {
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            onShowLicenseDialog()
                            onDismiss()
                        }) { Text(Strings.get(language, "license.have")) }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (!trialUsed) {
                                    val today = java.time.LocalDate.now().toString()
                                    LicenseManager.saveTrialStartDate(today)
                                    onDismiss()
                                }
                            },
                            enabled = !trialUsed
                        ) { Text(Strings.get(language, "license.trial")) }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = spectralPath,
                    onValueChange = onSpectralPathChange,
                    label = { Text(Strings.get(language, "spectral.selectFolder")) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Button(onClick = onShowSpectralChooser) { Text(Strings.get(language, "export.selectFolder")) }
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
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text(Strings.get(language, "config.close")) }
        }
    )
}
