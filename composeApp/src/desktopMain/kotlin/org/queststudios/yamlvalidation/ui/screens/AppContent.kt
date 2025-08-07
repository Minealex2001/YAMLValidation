package org.queststudios.yamlvalidation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.queststudios.yamlvalidation.ErrorBanner
import org.queststudios.yamlvalidation.MainCard
import org.queststudios.yamlvalidation.ResultsTabs
import org.queststudios.yamlvalidation.config.Configuration
import org.queststudios.yamlvalidation.core.ValidatorCore
import org.queststudios.yamlvalidation.i18n.Strings
import org.queststudios.yamlvalidation.licensing.LicenseManager
import org.queststudios.yamlvalidation.ui.ExpressiveTheme
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
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess

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
    var showUpdateDialog by remember { mutableStateOf(false) }
    var latestVersion by remember { mutableStateOf("") }
    var latestJarUrl by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                        var nombreYamlOriginal: String? = null
                        var renombrado = false
                        try {
                            if (yamlPath.isNotBlank()) {
                                val yaml = Yaml()
                                FileInputStream(yamlPath).use { input ->
                                    val data = yaml.load<Map<String, Any>>(input)
                                    val info = data["info"] as? Map<*, *>
                                    val title = info?.get("title") as? String
                                    if (!title.isNullOrBlank()) {
                                        nombreMicro = title.replace(" ", "_")
                                    }
                                }
                                yamlFile?.let {
                                    val nombreSinExtension = it.nameWithoutExtension
                                    if (nombreSinExtension != nombreMicro) {
                                        val nuevoArchivo = File(it.parent, "$nombreMicro.yaml")
                                        if (it.renameTo(nuevoArchivo)) {
                                            nombreYamlOriginal = it.name
                                            yamlFile = nuevoArchivo
                                            renombrado = true
                                        } else {
                                            errorBanner = "No se pudo renombrar el archivo YAML para Spectral."
                                        }
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                        val now = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")
                        val fechaHora = now.format(formatter)
                        val fileName = "${nombreMicro}-spectral-${fechaHora}.txt"
                        val outputFile = File(folder, fileName)
                        val validatorToUse = if (yamlFile != null && yamlFile!!.absolutePath != yamlPath) {
                            ValidatorCore(yamlFile!!.absolutePath, logger, rules, null, language, spectralPath)
                        } else validator
                        val resultExport = validatorToUse.exportSpectralToFile(outputFile.absolutePath)
                        if (renombrado && yamlFile != null && nombreYamlOriginal != null) {
                            val originalFile = File(yamlFile!!.parentFile ?: File("."), nombreYamlOriginal)
                            val restored = yamlFile!!.renameTo(originalFile)
                            if (!restored) {
                                errorBanner = "No se pudo restaurar el nombre original del archivo YAML."
                            } else {
                                if (yamlPath != originalFile.absolutePath) {
                                    yamlPath = originalFile.absolutePath
                                }
                            }
                        }
                        if (resultExport.success) {
                            showSpectralExportedDialog = true
                        } else {
                            errorBanner =
                                Strings.get(language, "export.error") + " " + (resultExport.errorMessage ?: "")
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

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Nueva versión disponible") },
            text = { Text("Hay una nueva versión ($latestVersion) disponible. ¿Deseas actualizar ahora?") },
            confirmButton = {
                Button(onClick = {
                    isUpdating = true
                    showUpdateDialog = false
                    scope.launch {
                        try {
                            println("[Updater] Iniciando descarga de la nueva versión desde: $latestJarUrl")
                            val jarPath = javaClass.protectionDomain.codeSource.location.toURI().path
                            println("[Updater] Path real del JAR en ejecución: $jarPath")
                            val jarFile = File(jarPath)
                            val tmpFile = File(jarFile.parentFile, "update_tmp.jar")
                            withContext(Dispatchers.IO) {
                                URL(latestJarUrl).openStream().use { input ->
                                    println("[Updater] Descargando archivo...")
                                    Files.copy(input, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                                    println("[Updater] Descarga completada: ${tmpFile.absolutePath}")
                                }
                            }
                            // Crear un script batch temporal para reemplazar el JAR y reiniciar
                            val batFile = File(jarFile.parentFile, "update_and_restart.bat")
                            batFile.writeText("""
                                @echo off
                                echo Esperando a que la app termine...
                                ping 127.0.0.1 -n 3 > nul
                                move /Y "${tmpFile.absolutePath}" "${jarFile.absolutePath}"
                                echo Lanzando la nueva versión...
                                start "" "javaw" -jar "${jarFile.absolutePath}"
                                del "%~f0"
                            """.trimIndent())
                            println("[Updater] Script batch creado: ${batFile.absolutePath}")
                            println("[Updater] Ejecutando script de actualización y saliendo...")
                            ProcessBuilder("cmd", "/c", batFile.absolutePath).start()
                            exitProcess(0)
                        } catch (e: Exception) {
                            println("[Updater] Error durante la descarga o actualización: ${e.message}")
                            e.printStackTrace()
                        } finally {
                            isUpdating = false
                        }
                    }
                }) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                Button(onClick = { showUpdateDialog = false }) {
                    Text("Más tarde")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        // Buscar config.properties en resources y luego en varios lugares del sistema de archivos
        var githubToken: String? = null
        val props = Properties()
        val resourceStream = javaClass.getResourceAsStream("/config.properties")
        if (resourceStream != null) {
            props.load(resourceStream)
            githubToken = props.getProperty("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }
            println("[Updater] Token leído desde resources/config.properties: ***")
        } else {
            val configPaths = listOf(
                "../../config.properties",
                "../config.properties",
                "config.properties",
                File(System.getProperty("user.dir"), "config.properties").absolutePath,
                File(File(System.getProperty("java.class.path")).parentFile, "config.properties").absolutePath
            )
            for (path in configPaths) {
                try {
                    FileInputStream(path).use { props.load(it) }
                    githubToken = props.getProperty("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }
                    if (githubToken != null) {
                        println("[Updater] Token leído desde $path: ***")
                        break
                    }
                } catch (e: Exception) {
                    println("[Updater] No se pudo leer $path: ${e.message}")
                }
            }
            if (githubToken == null) {
                println("[Updater] No se encontró config.properties con GITHUB_TOKEN en: $configPaths")
            }
        }
        // Leer versión actual del changelog desde resources
        val changelogStream = javaClass.getResourceAsStream("/changelog.md")
        val currentVersion = changelogStream?.bufferedReader()?.useLines { lines ->
            lines.firstOrNull { it.startsWith("## [") }?.let {
                val regex = Regex("\\[([0-9.]+)\\]")
                regex.find(it)?.groupValues?.getOrNull(1) ?: ""
            } ?: ""
        } ?: ""
        println("[Updater] Versión local: $currentVersion")
        // Consultar la última release de GitHub
        try {
            val apiUrl = "https://api.github.com/repos/Minealex2001/YAMLValidation/releases/latest"
            println("[Updater] Consultando la última release en: $apiUrl")
            val json = withContext(Dispatchers.IO) {
                val url = URL(apiUrl)
                val conn = url.openConnection() as HttpURLConnection
                if (githubToken != null) {
                    println("[Updater] Usando token de GitHub para autenticación.")
                    conn.setRequestProperty("Authorization", "Bearer $githubToken")
                }
                conn.connect()
                println("[Updater] Código de respuesta HTTP: ${conn.responseCode}")
                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().readText()
                    println("[Updater] Respuesta recibida correctamente.")
                    response
                } else {
                    val errorMsg = conn.errorStream?.bufferedReader()?.readText()
                    println("[Updater] Error al consultar la release: HTTP ${conn.responseCode} ${conn.responseMessage}. ${errorMsg ?: "No error body"}")
                    throw Exception("HTTP ${conn.responseCode}: ${conn.responseMessage}. ${errorMsg ?: "No error body"}")
                }
            }
            println("[Updater] JSON de GitHub: $json")
            // Extraer nombre de la release y versión
            val nameRegex = Regex(""""name": *"([^"]+)"""")
            val versionRegex = Regex("[Rr]elease[ _-]*v?([0-9.]+)")
            val name = nameRegex.find(json)?.groupValues?.getOrNull(1) ?: ""
            val jarRegex = Regex("https://[^\"]+\\.jar")
            val remoteVersion = versionRegex.find(name)?.groupValues?.getOrNull(1) ?: ""
            val jarUrl = jarRegex.find(json)?.value ?: ""
            println("[Updater] Nombre release: $name, Versión remota: $remoteVersion, jar: $jarUrl")
            if (remoteVersion.isNotEmpty() && jarUrl.isNotEmpty() && remoteVersion != currentVersion) {
                latestVersion = remoteVersion
                latestJarUrl = jarUrl
                showUpdateDialog = true
                println("[Updater] ¡Nueva versión encontrada!")
            } else {
                println("[Updater] No hay nueva versión.")
            }
        } catch (e: Exception) {
            if (e.message?.contains("HTTP 404") == true) {
                println("[Updater] No se encontró la release más reciente en GitHub. Puede que el repositorio sea privado, no exista, o no tenga releases publicados.")
            } else {
                println("[Updater] Error comprobando actualización: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
