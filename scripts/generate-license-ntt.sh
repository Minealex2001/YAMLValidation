#!/bin/bash

# Generador de licencias de NTT Data para Validador YAML
# Uso: ./generate-license-ntt.sh [random-param]

SECRET_NTT="VALIDADOR-YAML-2025-NTTDATA"
NTT_PREFIX="NTT:"

# Generar random param si no se proporciona
if [ -z "$1" ]; then
    RANDOM_PARAM=$(openssl rand -hex 8)
else
    RANDOM_PARAM="$1"
fi

# Crear el hash SHA-256
INPUT="${SECRET_NTT}-${RANDOM_PARAM}"
HASH=$(echo -n "$INPUT" | openssl dgst -sha256 -binary | base64)

# Construir la clave de licencia
LICENSE_KEY="${NTT_PREFIX}${HASH}:${RANDOM_PARAM}"

echo "============================================================"
echo "NTT Data License Key Generator"
echo "============================================================"
echo ""
echo "Random Parameter: $RANDOM_PARAM"
echo ""
echo "License Key:"
echo "$LICENSE_KEY"
echo ""
echo "============================================================"
echo ""
echo "Copy this license key and paste it in the application to activate NTT Data branding."
echo ""

