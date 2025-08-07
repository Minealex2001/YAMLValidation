package org.queststudios.yamlvalidation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import org.queststudios.yamlvalidation.ui.ExpressiveTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.queststudios.yamlvalidation.ErrorBanner
import org.queststudios.yamlvalidation.MainCard
import org.queststudios.yamlvalidation.ResultsTabs
import org.queststudios.yamlvalidation.config.Configuration
import org.queststudios.yamlvalidation.core.ValidatorCore
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.licensing.LicenseManager
import org.queststudios.yamlvalidation.ui.containers.AppContainer
import org.queststudios.yamlvalidation.ui.dialogs.ConfigDialog
import org.queststudios.yamlvalidation.ui.dialogs.LicenseDialog
import org.queststudios.yamlvalidation.ui.footers.AppFooter
import org.queststudios.yamlvalidation.validation.ComposeValidationLogger
import org.queststudios.yamlvalidation.validation.impl.*
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Este archivo orquesta la composición principal de la app, importando los componentes modulares.
 */
@Composable
fun AppContent() {
    var licenseValid by remember { mutableStateOf(LicenseManager.isLicenseValid()) }
    var trialStartDate by remember { mutableStateOf(LicenseManager.getTrialStartDate()) }
    val daysLeft = if (trialStartDate != null) 7 - ChronoUnit.DAYS.between(
        LocalDate.parse(trialStartDate!!),
        LocalDate.now()
    ).toInt() else 7
    val trialActive = !licenseValid && trialStartDate != null && daysLeft > 0
    val config = remember { Configuration() }
    // Forzar que el diálogo de licencia se muestre si no hay licencia ni trial, y nunca se cierre automáticamente por recomposición
    var showLicenseDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!licenseValid && !trialActive) {
            showLicenseDialog = true
        }
    }
    var showTrialExpiredDialog by remember { mutableStateOf(trialStartDate != null && daysLeft <= 0 && !licenseValid) }
    var licenseInput by remember { mutableStateOf("") }
    var licenseError by remember { mutableStateOf<String?>(null) }
    var spectralPath by remember { mutableStateOf(config.spectralPath) }
    var language by remember { mutableStateOf(config.language) }
    var spectralOutput by remember { mutableStateOf("") }
    var exportPath by remember { mutableStateOf("") }
    var errorBanner by remember { mutableStateOf<String?>(null) }
    var showYamlChooser by remember { mutableStateOf(false) }
    var showSpectralChooser by remember { mutableStateOf(false) }
    var showExportChooser by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    var showSpectralExportedDialog by remember { mutableStateOf(false) }
    var showSpectralDialog by remember { mutableStateOf(false) }
    var skipSpectralValidation by remember { mutableStateOf(false) }
    var pendingOpenSpectralChooser by remember { mutableStateOf(false) }
    var yamlPath by remember { mutableStateOf("") }
    val logger = remember { ComposeValidationLogger() }
    var yamlFile = if (yamlPath.isNotBlank()) File(yamlPath) else null
    var pendingOpenLicenseDialog by remember { mutableStateOf(false) }

    ExpressiveTheme {
        AppContainer {
            // Siempre renderizar la app, y mostrar el LicenseDialog como overlay si corresponde
            val rules = remember {
                listOf(
                    OperationIdValidation(),
                    AbsisOperationValidation(),
                    CertificationValidation(),
                    TypologyValidation(),
                    Codigo2xxValidation(),
                    RequestBodyValidation(),
                    Codigo400Validation()
                )
            }
            val validator = remember(yamlPath, language, rules, spectralPath) {
                ValidatorCore(yamlPath, logger, rules, null, language, spectralPath)
            }
            LaunchedEffect(spectralPath) { config.setSpectralPath(spectralPath) }
            LaunchedEffect(language) { config.setLanguage(language) }
            if (showYamlChooser) {
                LaunchedEffect(Unit) {
                    val chooser = JFileChooser()
                    chooser.dialogTitle = Strings.get(language, "file.open")
                    chooser.fileFilter = FileNameExtensionFilter("YAML (*.yaml, *.yml)", "yaml", "yml")
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    val result = chooser.showOpenDialog(null)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        yamlPath = chooser.selectedFile.absolutePath
                    }
                    showYamlChooser = false
                }
            }
            if (showSpectralChooser) {
                LaunchedEffect(Unit) {
                    if (spectralPath.isBlank()) {
                        errorBanner = Strings.get(language, "spectral.required")
                        showSpectralChooser = false
                        return@LaunchedEffect
                    }
                    val chooser = JFileChooser()
                    chooser.dialogTitle = Strings.get(language, "export.selectFolder")
                    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    val result = chooser.showOpenDialog(null)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        val folder = chooser.selectedFile
                        var nombreMicro = "microservicio"

                        try {
                            // 1. Leer el título del YAML para el nuevo nombre
                            if (yamlPath.isNotBlank()) {
                                val yaml = Yaml()
                                FileInputStream(yamlPath).use { input ->
                                    val data = yaml.load<Map<String, Any>>(input)
                                    val info = data["info"] as? Map<*, *>
                                    val title = info?.get("title") as? String
                                    if (!title.isNullOrBlank()) {
                                        nombreMicro = sanitizeFilename(title)
                                    }
                                }
                            }

                            // 2. Definir rutas de destino para el YAML y el TXT
                            val destYamlFile = File(folder, "$nombreMicro.yaml")
                            val now = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")
                            val fechaHora = now.format(formatter)
                            val outputTxtFile = File(folder, "${nombreMicro}-spectral-${fechaHora}.txt")

                            // 3. Copiar el YAML original a la nueva ubicación con el nuevo nombre
                            val originalFile = File(yamlPath)
                            try {
                                originalFile.copyTo(destYamlFile, overwrite = true)
                            } catch (copyEx: Exception) {
                                copyEx.printStackTrace()
                                errorBanner = "Error al copiar el archivo YAML: ${copyEx.message}"
                                return@launch
                            }

                            // 4. Ejecutar spectral en el archivo YAML copiado
                            val validatorForExport = ValidatorCore(destYamlFile.absolutePath, logger, rules, null, language, spectralPath)
                            val resultExport = validatorForExport.exportSpectralToFile(outputTxtFile.absolutePath)

                            if (resultExport.success) {
                                showSpectralExportedDialog = true
                            } else {
                                errorBanner = Strings.get(language, "export.error") + " " + (resultExport.errorMessage ?: "")
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            errorBanner = "Error durante la exportación: ${ex.message}"
                        }
                    }
                    showSpectralChooser = false
                }
            }
            if (showExportChooser) {
                LaunchedEffect(Unit) {
                    val chooser = JFileChooser()
                    chooser.dialogTitle = Strings.get(language, "export.dialogTitle")
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.selectedFile = File(Strings.get(language, "export.defaultFile"))
                    val result = chooser.showSaveDialog(null)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        exportPath = chooser.selectedFile.absolutePath
                        try {
                            FileWriter(exportPath).use { fw ->
                                validator.getLogHistory().forEach { fw.write(it + "\n") }
                                if (spectralOutput.isNotBlank()) {
                                    fw.write("\n--- ${Strings.get(language, "tab.spectralValidations")} ---\n")
                                    fw.write(spectralOutput)
                                }
                            }
                            errorBanner = Strings.get(language, "export.success") + " " + exportPath
                        } catch (e: Exception) {
                            errorBanner = Strings.get(language, "export.error") + " " + (e.message ?: "")
                        }
                    }
                    showExportChooser = false
                }
            }
            ConfigDialog(
                showConfigDialog = showConfigDialog,
                onDismiss = { showConfigDialog = false },
                licenseValid = licenseValid,
                trialStartDate = trialStartDate,
                onShowLicenseDialog = {
                    pendingOpenLicenseDialog = true
                    showConfigDialog = false
                },
                spectralPath = spectralPath,
                onSpectralPathChange = { spectralPath = it },
                onShowSpectralChooser = { showSpectralChooser = true },
                language = language,
                onLanguageChange = { language = it },
                config = config
            )
            if (showLicenseDialog || (!licenseValid && !trialActive)) {
                val trialUsed = trialStartDate != null
                LicenseDialog(
                    licenseInput = licenseInput,
                    onLicenseInputChange = { licenseInput = it },
                    licenseError = licenseError,
                    onActivate = {
                        if (LicenseManager.validateKey(licenseInput)) {
                            LicenseManager.saveLicenseKey(licenseInput)
                            licenseValid = true
                            showLicenseDialog = false
                        } else {
                            licenseError = Strings.get(language, "license.invalid")
                        }
                    },
                    onTrial = if (!trialActive) {
                        {
                            if (!trialUsed) {
                                val today = LocalDate.now().toString()
                                LicenseManager.saveTrialStartDate(today)
                                trialStartDate = today
                                showLicenseDialog = false
                            }
                        }
                    } else null,
                    trialActive = trialActive,
                    daysLeft = daysLeft,
                    trialUsed = trialUsed,
                    onDismiss = { showLicenseDialog = false }
                )
            }
            if (pendingOpenLicenseDialog && !showConfigDialog) {
                LaunchedEffect(pendingOpenLicenseDialog, showConfigDialog) {
                    showLicenseDialog = true
                    pendingOpenLicenseDialog = false
                }
            }
            if (showSpectralExportedDialog) {
                AlertDialog(
                    onDismissRequest = { showSpectralExportedDialog = false },
                    title = { Text(Strings.get(language, "export.spectral")) },
                    text = { Text(Strings.get(language, "spectral.export.success")) },
                    confirmButton = {
                        Button(onClick = { showSpectralExportedDialog = false }) {
                            Text(Strings.get(language, "ok"))
                        }
                    }
                )
            }
            if (showSpectralDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text(Strings.get(language, "export.spectral")) },
                    text = { Text(Strings.get(language, "spectral.askPath")) },
                    confirmButton = {
                        Button(onClick = {
                            showSpectralDialog = false
                            pendingOpenSpectralChooser = true
                        }) { Text(Strings.get(language, "spectral.btnSetPath")) }
                    },
                    dismissButton = {
                        Row {
                            Button(onClick = {
                                showSpectralDialog = false
                                skipSpectralValidation = true
                            }) { Text(Strings.get(language, "spectral.btnSkip")) }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                showSpectralDialog = false
                                config.setDontShowSpectralDialog(true)
                                skipSpectralValidation = true
                            }) { Text(Strings.get(language, "spectral.btnDontShow")) }
                        }
                    }
                )
            }
            if (pendingOpenSpectralChooser) {
                LaunchedEffect(true) {
                    showSpectralChooser = true
                    pendingOpenSpectralChooser = false
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF222831))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(Modifier.height(8.dp))
                    ErrorBanner(errorBanner) { errorBanner = null }
                    val coroutineScope = rememberCoroutineScope()
                    MainCard(
                        yamlPath = yamlPath,
                        onYamlPathChange = { yamlPath = it },
                        onYamlChooser = { showYamlChooser = true },
                        language = language,
                        logger = logger,
                        validator = validator,
                        onValidate = {
                            logger.logs.clear()
                            spectralOutput = ""
                            errorBanner = null
                            if (spectralPath.isBlank() && !config.dontShowSpectralDialog && !skipSpectralValidation) {
                                showSpectralDialog = true
                                return@MainCard
                            }
                            coroutineScope.launch {
                                val result = validator.runAllValidationsParallel()
                                if (!result.internal.success) {
                                    errorBanner = result.internal.errorMessage
                                }
                                spectralOutput = result.spectral
                            }
                        },
                        onSpectral = {
                            if (yamlPath.isBlank()) {
                                errorBanner = Strings.get(language, "error.noYaml")
                            } else if (spectralPath.isBlank()) {
                                errorBanner = Strings.get(language, "spectral.required")
                            } else {
                                showSpectralChooser = true
                            }
                        },
                        onExport = { showExportChooser = true },
                        onConfig = { showConfigDialog = true },
                        spectralPath = spectralPath,
                        showYamlChooser = showYamlChooser,
                        showSpectralChooser = showSpectralChooser,
                        showExportChooser = showExportChooser,
                        Strings = Strings
                    )
                    Spacer(Modifier.height(16.dp))
                    ResultsTabs(
                        selectedTab = selectedTab,
                        onTabChange = { selectedTab = it },
                        logger = logger,
                        spectralOutput = spectralOutput,
                        language = language,
                        Strings = Strings
                    )
                    Spacer(Modifier.height(8.dp))
                    AppFooter()
                }
            }
        }
    }
}
