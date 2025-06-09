package org.queststudios.yamlvalidation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.queststudios.yamlvalidation.core.ValidatorCore
import org.queststudios.yamlvalidation.validation.ComposeValidationLogger
import org.queststudios.yamlvalidation.validation.impl.OperationIdValidation
import org.queststudios.yamlvalidation.validation.impl.AbsisOperationValidation
import org.queststudios.yamlvalidation.validation.impl.CertificationValidation
import org.queststudios.yamlvalidation.validation.impl.TypologyValidation
import org.queststudios.yamlvalidation.validation.impl.Codigo2xxValidation
import org.queststudios.yamlvalidation.validation.impl.RequestBodyValidation
import org.queststudios.yamlvalidation.validation.impl.Codigo400Validation
import yamlvalidation.composeapp.generated.resources.Res
import yamlvalidation.composeapp.generated.resources.compose_multiplatform
import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileWriter
import org.queststudios.yamlvalidation.i18n.Strings
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.queststudios.yamlvalidation.config.Configuration
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.unit.DpSize
import org.queststudios.yamlvalidation.licensing.LicenseManager
import org.queststudios.yamlvalidation.ConfigDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var licenseValid by remember { mutableStateOf(LicenseManager.isLicenseValid()) }
    var trialStartDate by remember { mutableStateOf(LicenseManager.getTrialStartDate()) }
    val daysLeft = if (trialStartDate != null) 7 - java.time.temporal.ChronoUnit.DAYS.between(
        java.time.LocalDate.parse(trialStartDate),
        java.time.LocalDate.now()
    ).toInt() else 7
    val trialActive = !licenseValid && trialStartDate != null && daysLeft > 0
    var showLicenseDialog by remember { mutableStateOf(!licenseValid && !trialActive) }
    var showTrialExpiredDialog by remember { mutableStateOf(trialStartDate != null && daysLeft <= 0 && !licenseValid) }
    var licenseInput by remember { mutableStateOf("") }
    var licenseError by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF222831))
        ) {
            if (!licenseValid && !trialActive) {
                // Pantalla bloqueada/licencia
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Aquí puedes poner un logo o mensaje
                }
            } else {
                // Aquí va todo el contenido de la app
                val config = remember { Configuration() }
                var yamlPath by remember { mutableStateOf("") }
                var spectralPath by remember { mutableStateOf(config.spectralPath) }
                var language by remember { mutableStateOf(config.language) }
                val logger = remember { ComposeValidationLogger() }
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
                val validator = remember(yamlPath, language, rules) {
                    ValidatorCore(yamlPath, logger, rules, null, language)
                }
                var spectralOutput by remember { mutableStateOf("") }
                var exportPath by remember { mutableStateOf("") }
                var errorBanner by remember { mutableStateOf<String?>(null) }

                // Dialog state
                var showYamlChooser by remember { mutableStateOf(false) }
                var showSpectralChooser by remember { mutableStateOf(false) }
                var showExportChooser by remember { mutableStateOf(false) }
                var showConfigDialog by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableStateOf(0) }

                // Sincronizar cambios de configuración
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
                        val chooser = JFileChooser()
                        chooser.dialogTitle = Strings.get(language, "spectral.selectFolder")
                        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                        val result = chooser.showOpenDialog(null)
                        if (result == JFileChooser.APPROVE_OPTION) {
                            spectralPath = chooser.selectedFile.absolutePath
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

                // Configuración modal
                ConfigDialog(
                    showConfigDialog = showConfigDialog,
                    onDismiss = { showConfigDialog = false },
                    licenseValid = licenseValid,
                    trialStartDate = trialStartDate,
                    onShowLicenseDialog = {
                        showLicenseDialog = true
                        showConfigDialog = false
                    },
                    spectralPath = spectralPath,
                    onSpectralPathChange = { spectralPath = it },
                    onShowSpectralChooser = { showSpectralChooser = true },
                    language = language,
                    onLanguageChange = { language = it },
                    config = config
                )

                if (showLicenseDialog) {
                    // --- Lógica de prueba (trial) ---
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
                    val trialActive = !licenseValid && trialStart != null && daysLeft > 0
                    val trialUsed = trialStartDate != null
                    val licenseStatus = when {
                        licenseValid -> Strings.get(language, "license.activated")
                        trialActive -> Strings.get(language, "license.trialActive").replace("{0}", daysLeft.toString())
                        else -> Strings.get(language, "license.notActivated")
                    }
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text(Strings.get(language, "license.title")) },
                        text = {
                            Column {
                                Text(Strings.get(language, "license.intro"))
                                OutlinedTextField(
                                    value = licenseInput,
                                    onValueChange = { licenseInput = it },
                                    label = { Text(Strings.get(language, "license.key")) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (licenseError != null) Text(licenseError!!, color = Color.Red)
                                Spacer(Modifier.height(12.dp))
                                // --- Añadir botón de prueba de 7 días ---
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
                                val trialUsed = trialStartDate != null
                                val trialActive = !licenseValid && trialStartDate != null && daysLeft > 0
                                if (!trialActive) {
                                    Button(
                                        onClick = {
                                            if (!trialUsed) {
                                                val today = java.time.LocalDate.now().toString()
                                                LicenseManager.saveTrialStartDate(today)
                                                trialStartDate = today
                                                showLicenseDialog = false
                                            }
                                        },
                                        enabled = !trialUsed,
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text(Strings.get(language, "license.trial")) }
                                } else {
                                    Text(Strings.get(language, "license.trialActive").replace("{0}", daysLeft.toString()), color = Color(0xFF00ADB5), fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (LicenseManager.validateKey(licenseInput)) {
                                    LicenseManager.saveLicenseKey(licenseInput)
                                    licenseValid = true
                                    showLicenseDialog = false
                                } else {
                                    licenseError = Strings.get(language, "license.invalid")
                                }
                            }) { Text(Strings.get(language, "license.activate")) }
                        }
                    )
                }

                // --- Determinar si la prueba está activa para el bloqueo de la app ---
                val trialStartFile = File(System.getProperty("user.home") + File.separator + "Documentos" + File.separator + "ValidadorYAML" + File.separator + "trial_start.txt")
                val trialStart = if (trialStartFile.exists()) trialStartFile.readText().trim() else null
                val daysLeft = if (trialStart != null) 7 - java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.parse(trialStart),
                    java.time.LocalDate.now()
                ).toInt() else 7
                val trialActive = !licenseValid && trialStart != null && daysLeft > 0
                val trialUsed = trialStartDate != null

                if (!licenseValid && trialStartDate != null && daysLeft > 0) {
                    // Permitir que la app se renderice si la prueba está activa
                    showLicenseDialog = false
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
                            // Elimina el límite de ancho para que el contenido use todo el espacio disponible
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(Modifier.height(8.dp))
                        ErrorBanner(errorBanner) { errorBanner = null }
                        MainCard(
                            yamlPath = yamlPath,
                            onYamlPathChange = { yamlPath = it },
                            onYamlChooser = { showYamlChooser = true },
                            language = language,
                            logger = logger,
                            validator = validator,
                            onValidate = {
                                logger.logs.clear()
                                val result = validator.runAllValidations()
                                if (!result.success) {
                                    errorBanner = result.errorMessage
                                }
                            },
                            onSpectral = {
                                if (yamlPath.isNotEmpty() && spectralPath.isNotEmpty()) {
                                    try {
                                        val process = ProcessBuilder(
                                            "cmd", "/c", "spectral lint -r ./poc/.spectral_v2.yaml -f pretty \"$yamlPath\""
                                        ).directory(File(spectralPath)).redirectErrorStream(true).start()
                                        val output = process.inputStream.bufferedReader().use(BufferedReader::readText)
                                        val exitCode = process.waitFor()
                                        spectralOutput =
                                            "[Spectral exit code: $exitCode]\n" + output + if (exitCode != 0) "\n[ERROR] Spectral failed. Check if spectral is installed and the rule file exists." else ""
                                    } catch (e: Exception) {
                                        spectralOutput = Strings.get(language, "export.error") + " ${e.message}"
                                    }
                                } else {
                                    spectralOutput = Strings.get(language, "error.noYaml")
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
                if (showTrialExpiredDialog) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text(Strings.get(language, "trial.expired.title")) },
                        text = { Text(Strings.get(language, "trial.expired.text")) },
                        confirmButton = {
                            Button(onClick = { System.exit(0) }) { Text("OK") }
                        }
                    )
                }
            }
            if (showLicenseDialog) {
                // Definir trialUsed aquí para el scope correcto
                val trialUsed = trialStartDate != null
                LicenseDialog(
                    licenseInput = licenseInput,
                    onLicenseInputChange = { licenseInput = it },
                    licenseError = licenseError,
                    language = language,
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
                                val today = java.time.LocalDate.now().toString()
                                LicenseManager.saveTrialStartDate(today)
                                trialStartDate = today
                                showLicenseDialog = false
                            }
                        }
                    } else null,
                    trialActive = trialActive,
                    daysLeft = daysLeft,
                    trialUsed = trialUsed,
                )
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Validador YAML",
        state = rememberWindowState(
            width = 1600.dp,
            height = 1200.dp,
            position = WindowPosition.Aligned(Alignment.Center)
        ),
        resizable = true
    ) {
        this.window.minimumSize = java.awt.Dimension(1400, 900)
        Box(Modifier.fillMaxSize()) {
            App()
        }
    }
}