# Changelog
## [1.1.1] - 2025-08-07
### Funcionalidades
- Al exportar el resultado de Spectral a un archivo txt, ahora también se copia el YAML original en la misma ubicación, usando el mismo nombre que aparece en el contrato pero con extensión .yaml.
## [1.1.0] - 2025-08-06
### Funcionalidades
- Validación de tipo 'informational' en POST cuando el path termina en /request.
- Validación de año en x-certification.year para aceptar cualquier año de 4 dígitos.
- Al validar con Spectral sin exportar, el nombre se modifica temporalmente para evitar errores.
### Mejoras
- Mejoras internas en la gestión de validaciones y consistencia de reglas.
- Todos los mensajes de validación exitosa ahora se registran como SUCCESS en vez de INFO.
- Ahora los mensajes de la validación interna están traducidos a todos los idiomas.
### Soluciones
- Ahora, si no se especifica una ruta para Spectral, la aplicación muestra un mensaje de error en pantalla en vez de crashear.
- Se agregó validación global para evitar acciones dependientes de la ruta si esta no está definida.
## [1.0.2] - 2025-06-16
### Mejoras
- Mejorados las pantallas y consistencias entre ellas.
- Mejorada el tema y paletas de colores
### Soluciones
- Ahora el comando de spectral cierra siempre el terminal. [!1](https://github.com/Minealex2001/YAMLValidation/issues/1)
## [1.0.1.1] - 2025-06-10
### Soluciones
- Ahora el changelog está en formato es scrollable.
## [1.0.1] - 2025-06-10
### Funcionalidades
- Mejora en la validación de archivos YAML.
- Corrección de errores en la interfaz de usuario.
- Mejoras en la exportación de resultados a archivos de texto.
- Actualización de dependencias a versiones más recientes.
- Mejoras en la documentación del proyecto.
## [1.0.0] - 2025-06-09
### Funcionalidades
- Validación de archivos YAML.
- Interfaz para cargar y analizar archivos YAML.
- Reporte de errores de sintaxis en archivos YAML.
- Exportación de resultados a un archivo de texto.
- Soporte para Spectral.
- Exportacion de resultados de Spectral a un archivo de texto.
