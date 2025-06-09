package org.queststudios.yamlvalidation.i18n

object Strings {
    val es = mapOf(
        "app.title" to "Validador YAML",
        "file.label" to "Archivo YAML:",
        "file.open" to "Seleccionar YAML",
        "validate.button" to "Validar YAML",
        "export.spectral" to "Exportar resultado Spectral a archivo",
        "export.selectFolder" to "Seleccionar carpeta",
        "export.folderSelected" to "Carpeta seleccionada",
        "result.label" to "Resultado de la validación:",
        "output.export" to "Exportar salida a archivo",
        "error.noYaml" to "Debes seleccionar un archivo YAML.",
        "error.noFile" to "El archivo YAML no existe.",
        "error.noExportPath" to "Debes indicar la ubicación de exportación para Spectral.",
        "spectral.selectFolder" to "Selecciona la carpeta donde está el ejecutable de Spectral",
        "spectral.required" to "Debes seleccionar la carpeta de Spectral para continuar.",
        "export.success" to "Salida exportada correctamente a:",
        "export.error" to "Error al exportar la salida:",
        "export.dialogTitle" to "Guardar salida de validación como...",
        "export.defaultFile" to "salida_validacion.txt",
        "tab.appValidations" to "Validaciones de la aplicación",
        "tab.spectralValidations" to "Validaciones de Spectral",
        "config.open" to "Abrir configuración",
        "config.save" to "Guardar configuración",
        "config.close" to "Cerrar configuración",
        "config.title" to "Configuración",
        "menu.language" to "Idioma"
    )
    val en = mapOf(
        "app.title" to "YAML Validator",
        "file.label" to "YAML file:",
        "file.open" to "Open YAML",
        "validate.button" to "Validate YAML",
        "export.spectral" to "Export Spectral result to file",
        "export.selectFolder" to "Select folder",
        "export.folderSelected" to "Selected folder",
        "result.label" to "Validation result:",
        "output.export" to "Export output to file",
        "error.noYaml" to "You must select a YAML file.",
        "error.noFile" to "YAML file does not exist.",
        "error.noExportPath" to "You must select an export location for Spectral.",
        "spectral.selectFolder" to "Select the folder where the Spectral executable is located",
        "spectral.required" to "You must select the Spectral folder to continue.",
        "export.success" to "Output successfully exported to:",
        "export.error" to "Error exporting output:",
        "export.dialogTitle" to "Save validation output as...",
        "export.defaultFile" to "validation_output.txt",
        "tab.appValidations" to "Application Validations",
        "tab.spectralValidations" to "Spectral Validations",
        "config.open" to "Open configuration",
        "config.save" to "Save configuration",
        "config.close" to "Close configuration",
        "config.title" to "Configuration",
        "menu.language" to "Language"
    )
    val ca = mapOf(
        "app.title" to "Validació YAML",
        "file.label" to "Fitxer YAML:",
        "file.open" to "Obrir YAML",
        "validate.button" to "Validar YAML",
        "export.spectral" to "Exporta el resultat de Spectral a un fitxer",
        "export.selectFolder" to "Selecciona carpeta",
        "export.folderSelected" to "Carpeta seleccionada",
        "result.label" to "Resultat de la validació:",
        "output.export" to "Exporta la sortida a un fitxer",
        "error.noYaml" to "Has de seleccionar un fitxer YAML.",
        "error.noFile" to "El fitxer YAML no existeix.",
        "error.noExportPath" to "Has d'indicar la ubicació d'exportació per a Spectral.",
        "spectral.selectFolder" to "Selecciona la carpeta on està l'executable de Spectral",
        "spectral.required" to "Has de seleccionar la carpeta de Spectral per continuar.",
        "export.success" to "Sortida exportada correctament a:",
        "export.error" to "Error en exportar la sortida:",
        "export.dialogTitle" to "Desa la sortida de la validació com a...",
        "export.defaultFile" to "sortida_validacio.txt",
        "tab.appValidations" to "Validacions de l'aplicació",
        "tab.spectralValidations" to "Validacions de Spectral",
        "config.open" to "Obrir configuració",
        "config.save" to "Desar configuració",
        "config.close" to "Tancar configuració",
        "config.title" to "Configuració",
        "menu.language" to "Idioma"
    )
    fun get(lang: String, key: String): String {
        return when(lang) {
            "en" -> en[key] ?: key
            "ca" -> ca[key] ?: key
            else -> es[key] ?: key
        }
    }
}
