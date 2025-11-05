# Script de build para Windows que evita problemas de firma de código
$env:CSC_IDENTITY_AUTO_DISCOVERY = "false"
$env:SKIP_NOTARIZATION = "true"

# Limpiar caché de winCodeSign antes del build
$cachePath = "$env:LOCALAPPDATA\electron-builder\Cache\winCodeSign"
if (Test-Path $cachePath) {
    Write-Host "Limpiando caché de winCodeSign..."
    Remove-Item -Path $cachePath -Recurse -Force -ErrorAction SilentlyContinue
}

# Ejecutar el build
Write-Host "Iniciando build de Electron..."
npm run build:win

