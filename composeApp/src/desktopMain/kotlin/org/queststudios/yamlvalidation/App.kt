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
            // Si no hay licencia ni prueba activa, mostrar pantalla bloqueada
            if (!licenseValid && !trialActive) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Opcionalmente mostrar un mensaje o logo aquí
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
                if (showConfigDialog) {
                    AlertDialog(
                        onDismissRequest = { showConfigDialog = false },
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
                                    isActivated -> "✅ Activada"
                                    trialActive -> "🕒 Prueba (${daysLeft} días restantes)"
                                    else -> "❌ No activada"
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Licencia: $licenseStatus", fontWeight = FontWeight.Bold)
                                    if (!isActivated) {
                                        Spacer(Modifier.width(8.dp))
                                        Button(onClick = {
                                            showLicenseDialog = true
                                            showConfigDialog = false
                                        }) { Text("Tengo licencia") }
                                        Spacer(Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                if (!trialUsed) {
                                                    val today = java.time.LocalDate.now().toString()
                                                    LicenseManager.saveTrialStartDate(today)
                                                    trialStartDate = today
                                                    showConfigDialog = false
                                                }
                                            },
                                            enabled = !trialUsed
                                        ) { Text("No tengo licencia (Prueba 7 días)") }
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = spectralPath,
                                    onValueChange = { spectralPath = it },
                                    label = { Text(Strings.get(language, "spectral.selectFolder")) },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        Button(onClick = { showSpectralChooser = true }) { Text(Strings.get(language, "export.selectFolder")) }
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
                                        Button(onClick = { expanded = true }) { Text(language.uppercase()) }
                                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                            DropdownMenuItem(text = { Text("Español") }, onClick = { language = "es"; expanded = false; config.setLanguage("es") })
                                            DropdownMenuItem(text = { Text("English") }, onClick = { language = "en"; expanded = false; config.setLanguage("en") })
                                            DropdownMenuItem(text = { Text("Català") }, onClick = { language = "ca"; expanded = false; config.setLanguage("ca") })
                                        }
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showConfigDialog = false }) { Text(Strings.get(language, "config.close")) }
                        }
                    )
                }

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
                        licenseValid -> "✅ Activada"
                        trialActive -> "🕒 Prueba (${daysLeft} días restantes)"
                        else -> "❌ No activada"
                    }
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("Activación de licencia") },
                        text = {
                            Column {
                                Text("Introduce tu clave de licencia para activar el Validador YAML.")
                                OutlinedTextField(
                                    value = licenseInput,
                                    onValueChange = { licenseInput = it },
                                    label = { Text("Clave de licencia") },
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
                                    ) { Text("No tengo licencia (Prueba 7 días)") }
                                } else {
                                    Text("Prueba activa: $daysLeft días restantes", color = Color(0xFF00ADB5), fontWeight = FontWeight.Bold)
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
                                    licenseError = "Clave inválida. Solicita una clave válida."
                                }
                            }) { Text("Activar") }
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
                if (!licenseValid && !trialActive) {
                    Box(Modifier.fillMaxSize().background(Color(0xFF222831))) {}
                    return@MaterialTheme
                }

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
                        // Banner de error/éxito
                        AnimatedVisibility(errorBanner != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(), // Cambia de 0.5f a fillMaxWidth
                                colors = CardDefaults.cardColors(containerColor = if (errorBanner?.contains("error", true) == true) Color(0xFFFF5555) else Color(0xFF50DC78)),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(errorBanner ?: "", color = Color.White, modifier = Modifier.weight(1f))
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = { errorBanner = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF393E46))) {
                                        Text("OK", color = Color.White)
                                    }
                                }
                            }
                        }
                        // Card principal
                        Card(
                            modifier = Modifier.fillMaxWidth(), // Cambia de 0.5f a fillMaxWidth
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF393E46))
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    Strings.get(language, "app.title"),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFF00ADB5),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(20.dp))
                                OutlinedTextField(
                                    value = yamlPath,
                                    onValueChange = { yamlPath = it },
                                    label = { Text(Strings.get(language, "file.label")) },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        Button(onClick = { showYamlChooser = true }) { Text(Strings.get(language, "file.open")) }
                                    }
                                )
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(onClick = {
                                        logger.logs.clear()
                                        val result = validator.runAllValidations()
                                        if (!result.success) {
                                            errorBanner = result.errorMessage
                                        }
                                    }) {
                                        Text(Strings.get(language, "validate.button"))
                                    }
                                    Button(onClick = {
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
                                    }) {
                                        Text(Strings.get(language, "export.spectral"))
                                    }
                                    Button(onClick = { showExportChooser = true }) {
                                        Text(Strings.get(language, "output.export"))
                                    }
                                    Button(onClick = { showConfigDialog = true }) {
                                        Text(Strings.get(language, "config.open"))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Tabs de resultados
                        Card(
                            modifier = Modifier.fillMaxWidth(), // Cambia de 0.5f a fillMaxWidth
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF23272F))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                TabRow(selectedTabIndex = selectedTab) {
                                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(Strings.get(language, "tab.appValidations")) })
                                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(Strings.get(language, "tab.spectralValidations")) })
                                }
                                Spacer(Modifier.height(8.dp))
                                Box(Modifier.heightIn(min = 200.dp, max = 320.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                                    when (selectedTab) {
                                        0 -> Column(Modifier.fillMaxWidth()) {
                                            logger.logs.forEach { (level, msg) ->
                                                val color = when (level.uppercase()) {
                                                    "ERROR" -> Color(0xFFFF5555)
                                                    "WARNING", "WARN" -> Color(0xFFFFB43C)
                                                    "SUCCESS", "OK" -> Color(0xFF50DC78)
                                                    "INFO" -> Color(0xFF00ADB5)
                                                    else -> Color(0xFFECECEC)
                                                }
                                                Text("[$level] $msg", color = color)
                                            }
                                        }
                                        1 -> Column(Modifier.fillMaxWidth()) {
                                            if (spectralOutput.isNotBlank()) {
                                                Text(spectralOutput, color = Color(0xFFB0B0B0))
                                            } else {
                                                
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "© 2025 Alejandro Sánchez Pinto | alejandro.sanchezpinto@emeal.nttdata.com",
                            color = Color(0xFFAAAAAA),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Los diálogos siempre se renderizan encima de todo
            if (showLicenseDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Activación de licencia") },
                    text = {
                        Column {
                            Text("Introduce tu clave de licencia para activar el Validador YAML.")
                            OutlinedTextField(
                                value = licenseInput,
                                onValueChange = { licenseInput = it },
                                label = { Text("Clave de licencia") },
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
                                ) { Text("No tengo licencia (Prueba 7 días)") }
                            } else {
                                Text("Prueba activa: $daysLeft días restantes", color = Color(0xFF00ADB5), fontWeight = FontWeight.Bold)
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
                                licenseError = "Clave inválida. Solicita una clave válida."
                            }
                        }) { Text("Activar") }
                    }
                )
            }

            if (showTrialExpiredDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Prueba expirada") },
                    text = { Text("El periodo de prueba de 7 días ha expirado. Por favor, adquiere una licencia para continuar usando la aplicación.") },
                    confirmButton = {
                        Button(onClick = { System.exit(0) }) { Text("OK") }
                    }
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