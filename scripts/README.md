# Scripts de Generación de Licencias

Este directorio contiene scripts para generar licencias válidas para YAML Validator.

## Scripts Disponibles

### PowerShell (Windows)
```powershell
.\scripts\generate-license.ps1
```

O con un randomParam específico:
```powershell
.\scripts\generate-license.ps1 "MiParametro123"
```

### Bash (Linux/Mac)
```bash
chmod +x scripts/generate-license.sh
./scripts/generate-license.sh
```

O con un randomParam específico:
```bash
./scripts/generate-license.sh "MiParametro123"
```

### Node.js (Multiplataforma)
```bash
node scripts/generate-license.js
```

O con un randomParam específico:
```bash
node scripts/generate-license.js "MiParametro123"
```

## Cómo Funciona

El sistema de licencias usa:
- **SECRET**: `VALIDADOR-YAML-2025`
- **Formato**: `hashBase64:randomParam`
- **Hash**: SHA-256 de `SECRET-randomParam` codificado en Base64

## Ejemplo de Uso

1. Ejecuta el script para generar una licencia
2. Copia la clave de licencia generada
3. Pégalo en el diálogo de activación de la aplicación
4. La aplicación validará la licencia automáticamente

## Notas

- Si no proporcionas un `randomParam`, se generará uno aleatorio automáticamente
- Puedes usar el mismo `randomParam` para generar la misma licencia (útil para generar múltiples licencias con el mismo parámetro)
- La clave generada es válida para cualquier instalación de la aplicación

