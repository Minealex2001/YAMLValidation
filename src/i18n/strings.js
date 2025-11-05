export const Strings = {
  es: {
    "app.title": "Validador YAML",
    "file.label": "Archivo YAML",
    "file.open": "Seleccionar YAML",
    "validate.button": "Validar YAML",
    "export.spectral": "Exportar resultado Spectral a archivo",
    "export.selectFolder": "Seleccionar carpeta",
    "export.folderSelected": "Carpeta seleccionada",
    "result.label": "Resultado de la validación:",
    "output.export": "Exportar salida a archivo",
    "error.noYaml": "Debes seleccionar un archivo YAML.",
    "error.noFile": "El archivo YAML no existe.",
    "error.noExportPath": "Debes indicar la ubicación de exportación para Spectral.",
    "spectral.selectFolder": "Selecciona la carpeta donde está el ejecutable de Spectral",
    "spectral.required": "Debes seleccionar la carpeta de Spectral para continuar.",
    "export.success": "Salida exportada correctamente a:",
    "export.error": "Error al exportar la salida:",
    "export.dialogTitle": "Guardar salida de validación como...",
    "export.defaultFile": "salida_validacion.txt",
    "tab.appValidations": "Validaciones de la aplicación",
    "tab.spectralValidations": "Validaciones de Spectral",
    "config.open": "Abrir configuración",
    "config.save": "Guardar configuración",
    "config.close": "Cerrar configuración",
    "config.title": "Configuración",
    "menu.language": "Idioma",
    "language.name": "Español",
    "license.status": "Licencia",
    "license.have": "Tengo licencia",
    "license.trial": "No tengo licencia (Prueba 7 días)",
    "license.title": "Activación de licencia",
    "license.intro": "Introduce tu clave de licencia para activar el Validador YAML.",
    "license.key": "Clave de licencia",
    "license.invalid": "Clave inválida. Solicita una clave válida.",
    "license.activate": "Activar",
    "trial.expired.title": "Prueba expirada",
    "trial.expired.text": "El periodo de prueba de 7 días ha expirado. Por favor, adquiere una licencia para continuar usando la aplicación.",
    "license.activated": "✅ Activada",
    "license.notActivated": "❎ No activada",
    "license.trialActive": "⌛Prueba activa: {0} días restantes",
    "license.change": "Cambiar licencia",
    "license.deactivate": "Desactivar licencia",
    "license.activationError": "Error al activar la licencia. Por favor, verifica tu clave.",
    "license.trialUsed": "Prueba ya utilizada. Por favor, adquiere una licencia para continuar.",
    "validation_log_file_write_error": "Error al escribir en el archivo de registro de validación",
    "validation_yaml_file_not_found": "Archivo YAML no encontrado",
    "validation_yaml_missing_paths": "El archivo YAML debe contener la clave 'paths' para ser válido",
    "spectral.export.success": "Resultado Spectral exportado correctamente.",
    "ok": "OK",
    "help.button": "Ayuda",
    "help.title": "Ayuda",
    "config.changelog": "Ver Changelog",
    "spectral.askPath": "¿Deseas indicar la ruta de Spectral para ejecutar la validación?",
    "spectral.btnSetPath": "Especificar ruta",
    "spectral.btnSkip": "No",
    "spectral.btnDontShow": "No volver a mostrar",
    "typology.error.not_external": "x-typology.typology no es 'external': {0}",
    "typology.success.external": "Validación exitosa: x-typology.typology es 'external'.",
    "requestbody.error.not_defined": "requestBody no está definido para el método '{0}'.",
    "requestbody.success.defined": "Validación exitosa: requestBody está definido para el método '{0}' en el endpoint '{1}'.",
    "requestbody.error.defined_for_wrong_method": "requestBody está definido para el método '{0}', pero solo debería estar presente para métodos POST o PUT.",
    "requestbody.success.not_defined": "Validación exitosa: requestBody no está definido para el método '{0}' en el endpoint '{1}'.",
    "codigo400.success.domain_in_description": "Validación exitosa: La descripción del código '400' contiene el dominio en mayúsculas: {0}.",
    "codigo400.error.domain_not_in_description": "La descripción del código '400' no contiene el dominio en mayúsculas: {0}. Descripción: {1}",
    "codigo400.warning.description_not_defined": "La descripción del código '400' no está definida para el endpoint '{0}', método '{1}'.",
    "codigo2xx.success.found": "Validación exitosa: El endpoint '{0}' con método '{1}' tiene al menos un código de respuesta 2xx.",
    "codigo2xx.error.not_found": "El endpoint '{0}' con método '{1}' no tiene ningún código de respuesta 2xx (200, 201, 202, 204).",
    "certification.warning.not_empty": "x-certification.certification tiene el valor 'A'. Debería estar vacío si es la primera vez que se presenta a API Team CXB.",
    "certification.info.objective_not_a": "x-certification.objective debería ser 'A' la primera vez que se presenta. Valor actual: {0}",
    "certification.success.objective_a": "Validación exitosa: x-certification.objective es '{0}'.",
    "certification.success.year_valid": "Validación exitosa: x-certification.year tiene un año válido: {0}.",
    "certification.error.year_invalid": "x-certification.year no es un año válido: {0}",
    "absisop.success.get_informational": "Validación exitosa: x-absis-operation.type es 'informational' para método GET.",
    "absisop.error.get_type": "x-absis-operation.type valor actual: {0}, valor esperado 'informational'",
    "absisop.success.post_request_informational": "Validación exitosa: x-absis-operation.type es 'informational' para método POST con endpoint que termina en /request.",
    "absisop.error.post_request_type": "x-absis-operation.type valor actual: {0}, valor esperado: 'informational' para endpoint que termina en /request",
    "absisop.success.post_management": "Validación exitosa: x-absis-operation.type es 'management' para método POST.",
    "absisop.error.post_type": "x-absis-operation.type valor actual: {0} , valor esperado: 'management'",
    "absisop.success.put_management": "Validación exitosa: x-absis-operation.type es 'management' para método PUT.",
    "absisop.error.put_type": "x-absis-operation.type valor actual: {0} , valor esperado: 'management'",
    "absisop.success.security_format": "Validación exitosa: x-absis-operation.security tiene el formato correcto.",
    "absisop.error.security_format": "x-absis-operation.security no tiene el formato correcto: {0} (se esperaba: {1})",
    "absisop.error.security_undefined": "x-absis-operation.security no está definido.",
    "operationid.error.internal_prefix": "operationId no debe llevar prefijo 'internal' para endpoints que no contienen '/int'",
    "operationid.warning.not_defined": "operationId no está definido para endpoint."
  },
  en: {
    "app.title": "YAML Validator",
    "file.label": "YAML file",
    "file.open": "Open YAML",
    "validate.button": "Validate YAML",
    "export.spectral": "Export Spectral result to file",
    "export.selectFolder": "Select folder",
    "export.folderSelected": "Selected folder",
    "result.label": "Validation result:",
    "output.export": "Export output to file",
    "error.noYaml": "You must select a YAML file.",
    "error.noFile": "YAML file does not exist.",
    "error.noExportPath": "You must select an export location for Spectral.",
    "spectral.selectFolder": "Select the folder where the Spectral executable is located",
    "spectral.required": "You must select the Spectral folder to continue.",
    "export.success": "Output successfully exported to:",
    "export.error": "Error exporting output:",
    "export.dialogTitle": "Save validation output as...",
    "export.defaultFile": "validation_output.txt",
    "tab.appValidations": "Application Validations",
    "tab.spectralValidations": "Spectral Validations",
    "config.open": "Open configuration",
    "config.save": "Save configuration",
    "config.close": "Close configuration",
    "config.title": "Configuration",
    "menu.language": "Language",
    "language.name": "English",
    "license.status": "License",
    "license.have": "I have a license",
    "license.trial": "No license (7-day trial)",
    "license.title": "License activation",
    "license.intro": "Enter your license key to activate YAML Validator.",
    "license.key": "License key",
    "license.invalid": "Invalid key. Please request a valid key.",
    "license.activate": "Activate",
    "license.trialActive": "⌛Trial active: {0} days left",
    "trial.expired.title": "Trial expired",
    "trial.expired.text": "The 7-day trial period has expired. Please purchase a license to continue using the application.",
    "license.activated": "✅ Activated",
    "license.notActivated": "❎ Not activated",
    "license.trialActive": "⌛Trial active: {0} days left",
    "license.change": "Change license",
    "license.deactivate": "Deactivate license",
    "license.activationError": "Error activating license. Please check your key.",
    "license.trialUsed": "Trial already used. Please purchase a license to continue.",
    "validation_log_file_write_error": "Error writing validation log file",
    "validation_yaml_file_not_found": "YAML file not found",
    "validation_yaml_missing_paths": "The YAML file must contain the 'paths' key to be valid",
    "spectral.export.success": "Spectral result exported successfully.",
    "ok": "OK",
    "help.button": "Help",
    "help.title": "Help",
    "config.changelog": "View Changelog",
    "spectral.askPath": "Do you want to specify the Spectral path to run the validation?",
    "spectral.btnSetPath": "Set path",
    "spectral.btnSkip": "No",
    "spectral.btnDontShow": "Don't show again",
    "typology.error.not_external": "x-typology.typology is not 'external': {0}",
    "typology.success.external": "Successful validation: x-typology.typology is 'external'.",
    "requestbody.error.not_defined": "requestBody is not defined for method '{0}'.",
    "requestbody.success.defined": "Successful validation: requestBody is defined for method '{0}' in endpoint '{1}'.",
    "requestbody.error.defined_for_wrong_method": "requestBody is defined for method '{0}', but should only be present for POST or PUT methods.",
    "requestbody.success.not_defined": "Successful validation: requestBody is not defined for method '{0}' in endpoint '{1}'.",
    "codigo400.success.domain_in_description": "Successful validation: The description of code '400' contains the domain in uppercase: {0}.",
    "codigo400.error.domain_not_in_description": "The description of code '400' does not contain the domain in uppercase: {0}. Description: {1}",
    "codigo400.warning.description_not_defined": "The description of code '400' is not defined for endpoint '{0}', method '{1}'.",
    "codigo2xx.success.found": "Successful validation: The endpoint '{0}' with method '{1}' has at least one 2xx response code.",
    "codigo2xx.error.not_found": "The endpoint '{0}' with method '{1}' does not have any 2xx response code (200, 201, 202, 204).",
    "certification.warning.not_empty": "x-certification.certification has value 'A'. It should be empty if this is the first time submitting to API Team CXB.",
    "certification.info.objective_not_a": "x-certification.objective should be 'A' the first time it is submitted. Current value: {0}",
    "certification.success.objective_a": "Successful validation: x-certification.objective is '{0}'.",
    "certification.success.year_valid": "Successful validation: x-certification.year has a valid year: {0}.",
    "certification.error.year_invalid": "x-certification.year is not a valid year: {0}",
    "absisop.success.get_informational": "Successful validation: x-absis-operation.type is 'informational' for GET method.",
    "absisop.error.get_type": "x-absis-operation.type current value: {0}, expected 'informational'",
    "absisop.success.post_request_informational": "Successful validation: x-absis-operation.type is 'informational' for POST method with endpoint ending in /request.",
    "absisop.error.post_request_type": "x-absis-operation.type current value: {0}, expected: 'informational' for endpoint ending in /request",
    "absisop.success.post_management": "Successful validation: x-absis-operation.type is 'management' for POST method.",
    "absisop.error.post_type": "x-absis-operation.type current value: {0} , expected: 'management'",
    "absisop.success.put_management": "Successful validation: x-absis-operation.type is 'management' for PUT method.",
    "absisop.error.put_type": "x-absis-operation.type current value: {0} , expected: 'management'",
    "absisop.success.security_format": "Successful validation: x-absis-operation.security has the correct format.",
    "absisop.error.security_format": "x-absis-operation.security does not have the correct format: {0} (expected: {1})",
    "absisop.error.security_undefined": "x-absis-operation.security is not defined.",
    "operationid.error.internal_prefix": "operationId must not have 'internal' prefix for endpoints that do not contain '/int'",
    "operationid.warning.not_defined": "operationId is not defined for endpoint."
  },
  ca: {
    "app.title": "Validació YAML",
    "file.label": "Fitxer YAML",
    "file.open": "Obrir YAML",
    "validate.button": "Validar YAML",
    "export.spectral": "Exporta el resultat de Spectral a un fitxer",
    "export.selectFolder": "Selecciona carpeta",
    "export.folderSelected": "Carpeta seleccionada",
    "result.label": "Resultat de la validació:",
    "output.export": "Exporta la sortida a un fitxer",
    "error.noYaml": "Has de seleccionar un fitxer YAML.",
    "error.noFile": "El fitxer YAML no existeix.",
    "error.noExportPath": "Has d'indicar la ubicació d'exportació per a Spectral.",
    "spectral.selectFolder": "Selecciona la carpeta on està l'executable de Spectral",
    "spectral.required": "Has de seleccionar la carpeta de Spectral per continuar.",
    "export.success": "Sortida exportada correctament a:",
    "export.error": "Error en exportar la sortida:",
    "export.dialogTitle": "Desa la sortida de la validació com a...",
    "export.defaultFile": "sortida_validacio.txt",
    "tab.appValidations": "Validacions de l'aplicació",
    "tab.spectralValidations": "Validacions de Spectral",
    "config.open": "Obrir configuració",
    "config.save": "Desar configuració",
    "config.close": "Tancar configuració",
    "config.title": "Configuració",
    "menu.language": "Idioma",
    "language.name": "Català",
    "license.status": "Llicència",
    "license.have": "Tinc llicència",
    "license.trial": "No tinc llicència (Prova 7 dies)",
    "license.title": "Activació de llicència",
    "license.intro": "Introdueix la teva clau de llicència per activar el Validador YAML.",
    "license.key": "Clau de llicència",
    "license.invalid": "Clau invàlida. Sol·licita una clau vàlida.",
    "license.activate": "Activar",
    "license.trialActive": "⌛Prova activa: {0} dies restants",
    "trial.expired.title": "Prova expirada",
    "trial.expired.text": "El període de prova de 7 dies ha expirat. Si us plau, adquireix una llicència per continuar utilitzant l'aplicació.",
    "license.activated": "✅ Activada",
    "license.notActivated": "❎ No activada",
    "license.activationError": "Error en activar la llicència. Si us plau, comprova la teva clau.",
    "license.trialUsed": "Prova ja utilitzada. Si us plau, adquireix una llicència per continuar.",
    "validation_log_file_write_error": "Error al escriure al fitxer de registre de validació",
    "validation_yaml_file_not_found": "Fitxer YAML no trobat",
    "validation_yaml_missing_paths": "El fitxer YAML ha de contenir la clau 'paths' per ser vàlid",
    "spectral.export.success": "Resultat de Spectral exportat correctament.",
    "ok": "D'acord",
    "help.button": "Ajuda",
    "help.title": "Ajuda",
    "config.changelog": "Veure Changelog",
    "spectral.askPath": "Vols indicar la ruta de Spectral per executar la validació?",
    "spectral.btnSetPath": "Especificar ruta",
    "spectral.btnSkip": "No",
    "spectral.btnDontShow": "No tornar a mostrar",
    "typology.error.not_external": "x-typology.typology no és 'external': {0}",
    "typology.success.external": "Validació exitosa: x-typology.typology és 'external'.",
    "requestbody.error.not_defined": "requestBody no està definit per al mètode '{0}'.",
    "requestbody.success.defined": "Validació exitosa: requestBody està definit per al mètode '{0}' a l'endpoint '{1}'.",
    "requestbody.error.defined_for_wrong_method": "requestBody està definit per al mètode '{0}', però només hauria d'estar present per als mètodes POST o PUT.",
    "requestbody.success.not_defined": "Validació exitosa: requestBody no està definit per al mètode '{0}' a l'endpoint '{1}'.",
    "codigo400.success.domain_in_description": "Validació exitosa: La descripció del codi '400' conté el domini en majúscules: {0}.",
    "codigo400.error.domain_not_in_description": "La descripció del codi '400' no conté el domini en majúscules: {0}. Descripció: {1}",
    "codigo400.warning.description_not_defined": "La descripció del codi '400' no està definida per a l'endpoint '{0}', mètode '{1}'.",
    "codigo2xx.success.found": "Validació exitosa: L'endpoint '{0}' amb mètode '{1}' té almenys un codi de resposta 2xx.",
    "codigo2xx.error.not_found": "L'endpoint '{0}' amb mètode '{1}' no té cap codi de resposta 2xx (200, 201, 202, 204).",
    "certification.warning.not_empty": "x-certification.certification té el valor 'A'. Hauria d'estar buit si és la primera vegada que es presenta a API Team CXB.",
    "certification.info.objective_not_a": "x-certification.objective hauria de ser 'A' la primera vegada que es presenta. Valor actual: {0}",
    "certification.success.objective_a": "Validació exitosa: x-certification.objective és '{0}'.",
    "certification.success.year_valid": "Validació exitosa: x-certification.year té un any vàlid: {0}.",
    "certification.error.year_invalid": "x-certification.year no és un any vàlid: {0}",
    "absisop.success.get_informational": "Validació exitosa: x-absis-operation.type és 'informational' per al mètode GET.",
    "absisop.error.get_type": "x-absis-operation.type valor actual: {0}, valor esperat 'informational'",
    "absisop.success.post_request_informational": "Validació exitosa: x-absis-operation.type és 'informational' per al mètode POST amb endpoint que acaba en /request.",
    "absisop.error.post_request_type": "x-absis-operation.type valor actual: {0}, valor esperat: 'informational' per a endpoint que acaba en /request",
    "absisop.success.post_management": "Validació exitosa: x-absis-operation.type és 'management' per al mètode POST.",
    "absisop.error.post_type": "x-absis-operation.type valor actual: {0} , valor esperat: 'management'",
    "absisop.success.put_management": "Validació exitosa: x-absis-operation.type és 'management' per al mètode PUT.",
    "absisop.error.put_type": "x-absis-operation.type valor actual: {0} , valor esperat: 'management'",
    "absisop.success.security_format": "Validació exitosa: x-absis-operation.security té el format correcte.",
    "absisop.error.security_format": "x-absis-operation.security no té el format correcte: {0} (s'esperava: {1})",
    "absisop.error.security_undefined": "x-absis-operation.security no està definit.",
    "operationid.error.internal_prefix": "operationId no ha de portar el prefix 'internal' per a endpoints que no contenen '/int'",
    "operationid.warning.not_defined": "operationId no està definit per a l'endpoint."
  },
  
  get(lang, key, ...args) {
    const strings = this[lang] || this.es;
    let value = strings[key] || key;
    
    // Replace placeholders {0}, {1}, etc. with arguments
    for (let i = 0; i < args.length; i++) {
      value = value.replace(`{${i}}`, args[i]);
    }
    
    return value;
  },
  
  getHelpContent(lang) {
    const helpContent = {
      es: {
        title: "Ayuda - Cómo usar el Validador YAML",
        sections: [
          {
            icon: "description",
            title: "Introducción",
            content: [
              "El Validador YAML es una herramienta diseñada para validar archivos YAML de OpenAPI 3.x.x. Esta aplicación te permite verificar que tus especificaciones de API cumplan con los estándares y reglas de validación definidas.",
              "La aplicación realiza dos tipos de validaciones: validaciones internas de la aplicación y validaciones mediante Spectral CLI."
            ]
          },
          {
            icon: "folder_open",
            title: "Seleccionar archivo YAML",
            content: [
              "Para comenzar a validar, primero debes seleccionar un archivo YAML de OpenAPI:",
            ],
            list: [
              "Haz clic en el botón 'Seleccionar YAML' o escribe directamente la ruta del archivo en el campo de texto.",
              "El archivo debe ser un YAML válido de OpenAPI 3.x.x que contenga la clave 'paths'.",
              "Una vez seleccionado, el archivo quedará cargado y listo para validar."
            ]
          },
          {
            icon: "check_circle",
            title: "Validar YAML",
            content: [
              "El botón 'Validar YAML' ejecuta todas las validaciones disponibles:",
            ],
            list: [
              "Validaciones internas: Verifican reglas específicas como operationId, x-absis-operation, x-certification, x-typology, códigos de respuesta 2xx, códigos 400, y requestBody.",
              "Validaciones Spectral: Ejecuta Spectral CLI sobre tu archivo YAML para validaciones adicionales de OpenAPI.",
              "Los resultados se mostrarán en dos pestañas: 'Validaciones de la aplicación' y 'Validaciones de Spectral'."
            ]
          },
          {
            icon: "download",
            title: "Exportar resultado Spectral",
            content: [
              "Esta opción te permite exportar el resultado de Spectral a un archivo de texto:",
            ],
            list: [
              "Haz clic en 'Exportar resultado Spectral a archivo'.",
              "Selecciona la carpeta donde deseas guardar el archivo.",
              "El archivo se guardará con el nombre basado en el título del microservicio del YAML.",
              "También se copiará el YAML original en la misma ubicación con extensión .yaml."
            ]
          },
          {
            icon: "save",
            title: "Exportar salida a archivo",
            content: [
              "Exporta los resultados de las validaciones internas a un archivo de texto:",
            ],
            list: [
              "Haz clic en 'Exportar salida a archivo'.",
              "Selecciona la ubicación y nombre del archivo.",
              "El archivo contendrá todos los mensajes de validación (ERROR, WARNING, SUCCESS, INFO)."
            ]
          },
          {
            icon: "settings",
            title: "Configuración",
            content: [
              "En la configuración puedes ajustar:",
            ],
            list: [
              "Ruta de Spectral: Especifica la carpeta donde está instalado el ejecutable de Spectral CLI.",
              "Idioma: Cambia el idioma de la interfaz (Español, English, Català).",
              "Licencia: Activa tu licencia o inicia el periodo de prueba de 7 días."
            ]
          },
          {
            icon: "rule",
            title: "Reglas de validación",
            content: [
              "Las validaciones internas verifican las siguientes reglas:",
            ],
            list: [
              "operationId: Debe estar definido y no usar el prefijo 'internal' para endpoints que no contienen '/int'.",
              "x-absis-operation: Verifica que el tipo sea 'informational' para GET, 'management' para POST/PUT, y 'informational' para POST en endpoints que terminan en /request.",
              "x-certification: Valida que objective sea 'A' la primera vez y que year sea un año válido de 4 dígitos.",
              "x-typology: Debe ser 'external'.",
              "Códigos 2xx: Debe existir al menos un código de respuesta 2xx (200, 201, 202, 204).",
              "Código 400: La descripción debe contener el dominio funcional en mayúsculas.",
              "requestBody: Debe estar definido para métodos POST y PUT, y no estar presente para otros métodos."
            ]
          },
          {
            icon: "info",
            title: "Resultados de validación",
            content: [
              "Los resultados se muestran con diferentes niveles:",
            ],
            list: [
              "SUCCESS (verde): La validación pasó correctamente.",
              "ERROR (rojo): Se encontró un error que debe corregirse.",
              "WARNING (amarillo): Advertencia que debería revisarse.",
              "INFO (azul): Información adicional sobre la validación."
            ]
          },
          {
            icon: "key",
            title: "Sistema de licencias",
            content: [
              "La aplicación requiere una licencia para su uso:",
            ],
            list: [
              "Prueba gratuita: Dispones de 7 días de prueba desde el primer uso.",
              "Licencia completa: Activa tu licencia introduciendo la clave de licencia en Configuración > Licencia.",
              "El estado de la licencia se muestra en la pantalla de configuración."
            ]
          }
        ]
      },
      en: {
        title: "Help - How to use YAML Validator",
        sections: [
          {
            icon: "description",
            title: "Introduction",
            content: [
              "YAML Validator is a tool designed to validate OpenAPI 3.x.x YAML files. This application allows you to verify that your API specifications comply with defined validation standards and rules.",
              "The application performs two types of validations: internal application validations and validations using Spectral CLI."
            ]
          },
          {
            icon: "folder_open",
            title: "Select YAML file",
            content: [
              "To start validating, you must first select an OpenAPI YAML file:",
            ],
            list: [
              "Click the 'Open YAML' button or type the file path directly in the text field.",
              "The file must be a valid OpenAPI 3.x.x YAML that contains the 'paths' key.",
              "Once selected, the file will be loaded and ready to validate."
            ]
          },
          {
            icon: "check_circle",
            title: "Validate YAML",
            content: [
              "The 'Validate YAML' button runs all available validations:",
            ],
            list: [
              "Internal validations: Verify specific rules such as operationId, x-absis-operation, x-certification, x-typology, 2xx response codes, 400 codes, and requestBody.",
              "Spectral validations: Runs Spectral CLI on your YAML file for additional OpenAPI validations.",
              "Results will be displayed in two tabs: 'Application Validations' and 'Spectral Validations'."
            ]
          },
          {
            icon: "download",
            title: "Export Spectral result",
            content: [
              "This option allows you to export the Spectral result to a text file:",
            ],
            list: [
              "Click 'Export Spectral result to file'.",
              "Select the folder where you want to save the file.",
              "The file will be saved with a name based on the microservice title from the YAML.",
              "The original YAML will also be copied to the same location with .yaml extension."
            ]
          },
          {
            icon: "save",
            title: "Export output to file",
            content: [
              "Exports the internal validation results to a text file:",
            ],
            list: [
              "Click 'Export output to file'.",
              "Select the location and filename.",
              "The file will contain all validation messages (ERROR, WARNING, SUCCESS, INFO)."
            ]
          },
          {
            icon: "settings",
            title: "Configuration",
            content: [
              "In configuration you can adjust:",
            ],
            list: [
              "Spectral path: Specify the folder where the Spectral CLI executable is installed.",
              "Language: Change the interface language (Spanish, English, Catalan).",
              "License: Activate your license or start the 7-day trial period."
            ]
          },
          {
            icon: "rule",
            title: "Validation rules",
            content: [
              "Internal validations verify the following rules:",
            ],
            list: [
              "operationId: Must be defined and not use the 'internal' prefix for endpoints that do not contain '/int'.",
              "x-absis-operation: Verifies that the type is 'informational' for GET, 'management' for POST/PUT, and 'informational' for POST in endpoints ending in /request.",
              "x-certification: Validates that objective is 'A' the first time and that year is a valid 4-digit year.",
              "x-typology: Must be 'external'.",
              "2xx codes: At least one 2xx response code (200, 201, 202, 204) must exist.",
              "400 code: The description must contain the functional domain in uppercase.",
              "requestBody: Must be defined for POST and PUT methods, and not present for other methods."
            ]
          },
          {
            icon: "info",
            title: "Validation results",
            content: [
              "Results are displayed with different levels:",
            ],
            list: [
              "SUCCESS (green): Validation passed correctly.",
              "ERROR (red): An error was found that must be corrected.",
              "WARNING (yellow): Warning that should be reviewed.",
              "INFO (blue): Additional information about the validation."
            ]
          },
          {
            icon: "key",
            title: "License system",
            content: [
              "The application requires a license for use:",
            ],
            list: [
              "Free trial: You have 7 days of trial from first use.",
              "Full license: Activate your license by entering the license key in Configuration > License.",
              "License status is shown on the configuration screen."
            ]
          }
        ]
      },
      ca: {
        title: "Ajuda - Com utilitzar el Validador YAML",
        sections: [
          {
            icon: "description",
            title: "Introducció",
            content: [
              "El Validador YAML és una eina dissenyada per validar fitxers YAML d'OpenAPI 3.x.x. Aquesta aplicació et permet verificar que les teves especificacions d'API compleixin amb els estàndards i regles de validació definits.",
              "L'aplicació realitza dos tipus de validacions: validacions internes de l'aplicació i validacions mitjançant Spectral CLI."
            ]
          },
          {
            icon: "folder_open",
            title: "Seleccionar fitxer YAML",
            content: [
              "Per començar a validar, primer has de seleccionar un fitxer YAML d'OpenAPI:",
            ],
            list: [
              "Fes clic al botó 'Obrir YAML' o escriu directament la ruta del fitxer al camp de text.",
              "El fitxer ha de ser un YAML vàlid d'OpenAPI 3.x.x que contingui la clau 'paths'.",
              "Un cop seleccionat, el fitxer quedarà carregat i llest per validar."
            ]
          },
          {
            icon: "check_circle",
            title: "Validar YAML",
            content: [
              "El botó 'Validar YAML' executa totes les validacions disponibles:",
            ],
            list: [
              "Validacions internes: Verifiquen regles específiques com operationId, x-absis-operation, x-certification, x-typology, codis de resposta 2xx, codis 400, i requestBody.",
              "Validacions Spectral: Executa Spectral CLI sobre el teu fitxer YAML per validacions addicionals d'OpenAPI.",
              "Els resultats es mostraran en dues pestanyes: 'Validacions de l'aplicació' i 'Validacions de Spectral'."
            ]
          },
          {
            icon: "download",
            title: "Exportar resultat Spectral",
            content: [
              "Aquesta opció et permet exportar el resultat de Spectral a un fitxer de text:",
            ],
            list: [
              "Fes clic a 'Exporta el resultat de Spectral a un fitxer'.",
              "Selecciona la carpeta on desitges desar el fitxer.",
              "El fitxer es desarà amb el nom basat en el títol del microservei del YAML.",
              "També es copiarà el YAML original a la mateixa ubicació amb extensió .yaml."
            ]
          },
          {
            icon: "save",
            title: "Exportar sortida a fitxer",
            content: [
              "Exporta els resultats de les validacions internes a un fitxer de text:",
            ],
            list: [
              "Fes clic a 'Exporta la sortida a un fitxer'.",
              "Selecciona la ubicació i nom del fitxer.",
              "El fitxer contindrà tots els missatges de validació (ERROR, WARNING, SUCCESS, INFO)."
            ]
          },
          {
            icon: "settings",
            title: "Configuració",
            content: [
              "A la configuració pots ajustar:",
            ],
            list: [
              "Ruta de Spectral: Especifica la carpeta on està instal·lat l'executable de Spectral CLI.",
              "Idioma: Canvia l'idioma de la interfície (Español, English, Català).",
              "Llicència: Activa la teva llicència o inicia el període de prova de 7 dies."
            ]
          },
          {
            icon: "rule",
            title: "Regles de validació",
            content: [
              "Les validacions internes verifiquen les següents regles:",
            ],
            list: [
              "operationId: Ha d'estar definit i no usar el prefix 'internal' per a endpoints que no contenen '/int'.",
              "x-absis-operation: Verifica que el tipus sigui 'informational' per a GET, 'management' per a POST/PUT, i 'informational' per a POST en endpoints que acaben en /request.",
              "x-certification: Valida que objective sigui 'A' la primera vegada i que year sigui un any vàlid de 4 dígits.",
              "x-typology: Ha de ser 'external'.",
              "Codis 2xx: Ha d'existir almenys un codi de resposta 2xx (200, 201, 202, 204).",
              "Codi 400: La descripció ha de contenir el domini funcional en majúscules.",
              "requestBody: Ha d'estar definit per als mètodes POST i PUT, i no estar present per a altres mètodes."
            ]
          },
          {
            icon: "info",
            title: "Resultats de validació",
            content: [
              "Els resultats es mostren amb diferents nivells:",
            ],
            list: [
              "SUCCESS (verd): La validació va passar correctament.",
              "ERROR (vermell): S'ha trobat un error que s'ha de corregir.",
              "WARNING (groc): Advertència que hauria de revisar-se.",
              "INFO (blau): Informació addicional sobre la validació."
            ]
          },
          {
            icon: "key",
            title: "Sistema de llicències",
            content: [
              "L'aplicació requereix una llicència per al seu ús:",
            ],
            list: [
              "Prova gratuïta: Disposes de 7 dies de prova des del primer ús.",
              "Llicència completa: Activa la teva llicència introduint la clau de llicència a Configuració > Llicència.",
              "L'estat de la llicència es mostra a la pantalla de configuració."
            ]
          }
        ]
      }
    };
    
    return helpContent[lang] || helpContent.es;
  }
};

