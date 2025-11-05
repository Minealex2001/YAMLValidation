#!/bin/bash
# Script para generar licencias para YAML Validator
# Uso: ./scripts/generate-license.sh [randomParam]
# Si no se proporciona randomParam, se generará uno aleatorio

SECRET="VALIDADOR-YAML-2025"

# Si no se proporciona randomParam, generar uno aleatorio
if [ -z "$1" ]; then
    RANDOM_PARAM=$(cat /dev/urandom | tr -dc 'A-Za-z0-9' | fold -w 16 | head -n 1)
    echo "Generando randomParam aleatorio: $RANDOM_PARAM"
else
    RANDOM_PARAM="$1"
fi

# Calcular hash SHA-256
INPUT="$SECRET-$RANDOM_PARAM"
HASH_BASE64=$(echo -n "$INPUT" | openssl dgst -sha256 -binary | openssl base64)

# Generar clave de licencia
LICENSE_KEY="$HASH_BASE64:$RANDOM_PARAM"

echo ""
echo "========================================="
echo "LICENCIA GENERADA"
echo "========================================="
echo ""
echo "Clave de licencia:"
echo "$LICENSE_KEY"
echo ""
echo "Random Param:"
echo "$RANDOM_PARAM"
echo ""
echo "========================================="
echo ""

# Copiar al portapapeles si está disponible (solo en Linux con xclip o macOS con pbcopy)
if command -v xclip &> /dev/null; then
    echo -n "$LICENSE_KEY" | xclip -selection clipboard
    echo "Clave copiada al portapapeles!"
elif command -v pbcopy &> /dev/null; then
    echo -n "$LICENSE_KEY" | pbcopy
    echo "Clave copiada al portapapeles!"
else
    echo "Usa Ctrl+C para copiar manualmente"
fi

echo ""
echo "Para usar esta licencia, cópiala en el diálogo de activación de la aplicación."

