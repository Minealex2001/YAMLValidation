# Generador de licencias de NTT Data para Validador YAML
# Uso: .\generate-license-ntt.ps1 [random-param]

param(
    [string]$RandomParam = ""
)

$SECRET_NTT = "VALIDADOR-YAML-2025-NTTDATA"
$NTT_PREFIX = "NTT:"

# Generar random param si no se proporciona
if ([string]::IsNullOrEmpty($RandomParam)) {
    $RandomParam = -join ((48..57) + (97..122) | Get-Random -Count 13 | ForEach-Object {[char]$_})
}

# Crear el hash SHA-256
$inputString = "${SECRET_NTT}-${RandomParam}"
$inputBytes = [System.Text.Encoding]::UTF8.GetBytes($inputString)
$hash = [System.Security.Cryptography.SHA256]::Create().ComputeHash($inputBytes)

# Convertir a Base64 exactamente como btoa(String.fromCharCode(...hashArray))
$charArray = $hash | ForEach-Object { [char]$_ }
$charString = -join $charArray
# Usar ISO-8859-1 (Latin-1) para codificar, que es lo que usa JavaScript
$latin1 = [System.Text.Encoding]::GetEncoding("ISO-8859-1")
$charBytes = $latin1.GetBytes($charString)
$hashBase64 = [Convert]::ToBase64String($charBytes)

# Construir la clave de licencia
$licenseKey = "${NTT_PREFIX}${hashBase64}:${RandomParam}"

Write-Host "=" * 60 -ForegroundColor Cyan
Write-Host "NTT Data License Key Generator" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan
Write-Host ""
Write-Host "Random Parameter: " -NoNewline
Write-Host $RandomParam -ForegroundColor Yellow
Write-Host ""
Write-Host "License Key:" -ForegroundColor Green
Write-Host $licenseKey -ForegroundColor White
Write-Host ""
Write-Host "=" * 60 -ForegroundColor Cyan
Write-Host ""
Write-Host "Copy this license key and paste it in the application to activate NTT Data branding." -ForegroundColor Gray
Write-Host ""

