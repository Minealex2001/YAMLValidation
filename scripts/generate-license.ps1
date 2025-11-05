# Script para generar licencias para YAML Validator
# Uso: .\scripts\generate-license.ps1 [randomParam]
# Si no se proporciona randomParam, se generará uno aleatorio

param(
    [string]$RandomParam = ""
)

$SECRET = "VALIDADOR-YAML-2025"

# Si no se proporciona randomParam, generar uno aleatorio
if ([string]::IsNullOrEmpty($RandomParam)) {
    $RandomParam = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 16 | ForEach-Object {[char]$_})
    Write-Host "Generando randomParam aleatorio: $RandomParam" -ForegroundColor Yellow
}

# Calcular hash SHA-256 (exactamente como en LicenseManager.js)
$input = "$SECRET-$RandomParam"
$bytes = [System.Text.Encoding]::UTF8.GetBytes($input)
$sha256 = [System.Security.Cryptography.SHA256]::Create()
$hashBytes = $sha256.ComputeHash($bytes)

# Convertir a Base64 exactamente como btoa(String.fromCharCode(...hashArray))
# btoa codifica cada byte (0-255) directamente como un carácter Latin-1
# y luego convierte esa cadena a Base64
$charArray = $hashBytes | ForEach-Object { [char]$_ }
$charString = -join $charArray
# Usar ISO-8859-1 (Latin-1) para codificar, que es lo que usa JavaScript
$latin1 = [System.Text.Encoding]::GetEncoding("ISO-8859-1")
$charBytes = $latin1.GetBytes($charString)
$hashBase64 = [Convert]::ToBase64String($charBytes)

# Generar clave de licencia
$licenseKey = "${hashBase64}:${RandomParam}"

Write-Host ""
Write-Host "=========================================" -ForegroundColor Green
Write-Host "LICENCIA GENERADA" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Clave de licencia:" -ForegroundColor Cyan
Write-Host $licenseKey -ForegroundColor White
Write-Host ""
Write-Host "Random Param:" -ForegroundColor Cyan
Write-Host $RandomParam -ForegroundColor White
Write-Host ""
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""

# Copiar al portapapeles si está disponible
try {
    $licenseKey | Set-Clipboard
    Write-Host "Clave copiada al portapapeles!" -ForegroundColor Green
} catch {
    Write-Host "No se pudo copiar al portapapeles (usa Ctrl+C para copiar manualmente)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Para usar esta licencia, cópiala en el diálogo de activación de la aplicación." -ForegroundColor Gray

