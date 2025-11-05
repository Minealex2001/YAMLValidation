# YAML Validation - Electron App

Esta es la aplicación de validación YAML desarrollada con Electron, completamente migrada a JavaScript/TypeScript para ser compatible con cualquier versión de Java y tener mejor distribución multiplataforma.

## Requisitos

- Node.js (v16 o superior)
- npm o yarn

## Instalación

```bash
npm install
```

## Desarrollo

Para ejecutar la aplicación en modo desarrollo:

```bash
npm run dev
```

## Build

Para construir la aplicación para distribución:

```bash
# Todas las plataformas
npm run build

# Solo Windows
npm run build:win

# Solo macOS
npm run build:mac

# Solo Linux
npm run build:linux
```

Los archivos de distribución se generarán en la carpeta `dist/`.

## Estructura del Proyecto

```
YAMLValidation/
├── electron/
│   ├── main.js          # Proceso principal de Electron
│   └── preload.js       # Script de preload para IPC
├── src/
│   ├── index.html       # HTML principal
│   ├── styles.css       # Estilos CSS
│   ├── app.js           # Lógica principal de la aplicación
│   ├── core/            # Lógica de validación
│   ├── validation/      # Reglas de validación
│   ├── licensing/       # Sistema de licencias
│   ├── config/          # Configuración
│   ├── i18n/            # Internacionalización
│   └── ui/              # Componentes UI
├── scripts/             # Scripts de utilidad (generación de licencias)
├── package.json
└── README.md
```

## Características

- ✅ Validación YAML completa
- ✅ Integración con Spectral CLI
- ✅ Sistema de licencias con periodo de prueba
- ✅ Internacionalización (ES, EN, CA)
- ✅ Interfaz moderna con Material Design 3
- ✅ Modo oscuro/claro
- ✅ Multiplataforma (Windows, macOS, Linux)
- ✅ Barra de título personalizada

## Notas

- La aplicación requiere que Spectral esté instalado como proceso externo
- La configuración se guarda usando `electron-store`
- Los archivos de licencia se almacenan en el almacenamiento de Electron
