// Script para verificar que una licencia generada sea válida
// Simula exactamente el comportamiento de LicenseManager.js

const crypto = require('crypto');

const SECRET = 'VALIDADOR-YAML-2025';

// Función que simula exactamente generateKey de LicenseManager.js
function generateKeyWebCryptoStyle(randomParam) {
  const input = `${SECRET}-${randomParam}`;
  const encoder = new TextEncoder();
  const data = encoder.encode(input);
  
  // Simular crypto.subtle.digest usando Node.js crypto
  const hashBuffer = crypto.createHash('sha256').update(data).digest();
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  
  // Simular btoa(String.fromCharCode(...hashArray))
  // btoa espera una cadena de caracteres, así que convertimos cada byte a char
  const charString = String.fromCharCode(...hashArray);
  const hashBase64 = Buffer.from(charString, 'binary').toString('base64');
  
  return `${hashBase64}:${randomParam}`;
}

// Función que valida una clave (como validateKey en LicenseManager.js)
function validateKey(key) {
  const parts = key.split(':');
  if (parts.length !== 2) return false;
  
  const [hashBase64, randomParam] = parts;
  const expected = generateKeyWebCryptoStyle(randomParam);
  return key === expected;
}

// Probar con una clave generada
const testKey = process.argv[2];
if (!testKey) {
  console.log('Uso: node scripts/verify-license.js "hashBase64:randomParam"');
  process.exit(1);
}

console.log('Verificando clave:', testKey);
const isValid = validateKey(testKey);
console.log('¿Es válida?', isValid ? 'SÍ' : 'NO');

if (!isValid) {
  const parts = testKey.split(':');
  if (parts.length === 2) {
    const [hashBase64, randomParam] = parts;
    const expected = generateKeyWebCryptoStyle(randomParam);
    console.log('');
    console.log('Clave proporcionada:', testKey);
    console.log('Clave esperada:     ', expected);
    console.log('Random Param:', randomParam);
  }
}

