// Script de prueba para verificar que la generación de licencias funciona correctamente
const crypto = require('crypto');

const SECRET = 'VALIDADOR-YAML-2025';
const randomParam = 'test123';

// Método 1: Como en el script (Node.js crypto)
const input1 = `${SECRET}-${randomParam}`;
const hash1 = crypto.createHash('sha256').update(input1).digest();
const hashBase64_1 = hash1.toString('base64');
const licenseKey1 = `${hashBase64_1}:${randomParam}`;

console.log('Método 1 (Node.js crypto):');
console.log('Input:', input1);
console.log('Hash (hex):', hash1.toString('hex'));
console.log('Hash (base64):', hashBase64_1);
console.log('License Key:', licenseKey1);
console.log('');

// Método 2: Como en LicenseManager.js (Web Crypto API simulado)
async function testWebCrypto() {
  const input2 = `${SECRET}-${randomParam}`;
  const encoder = new TextEncoder();
  const data = encoder.encode(input2);
  
  // Simular crypto.subtle.digest en Node.js
  const hash2 = crypto.createHash('sha256').update(data).digest();
  const hashArray = Array.from(new Uint8Array(hash2));
  const hashBase64_2 = Buffer.from(hashArray).toString('base64');
  
  // O usando btoa equivalente
  const hashBase64_3 = Buffer.from(hash2).toString('base64');
  
  const licenseKey2 = `${hashBase64_2}:${randomParam}`;
  const licenseKey3 = `${hashBase64_3}:${randomParam}`;

  console.log('Método 2 (simulando Web Crypto API):');
  console.log('Input:', input2);
  console.log('Hash (hex):', hash2.toString('hex'));
  console.log('Hash (base64 - método array):', hashBase64_2);
  console.log('Hash (base64 - método buffer):', hashBase64_3);
  console.log('License Key (array):', licenseKey2);
  console.log('License Key (buffer):', licenseKey3);
  console.log('');
  
  console.log('Comparación:');
  console.log('Método 1 === Método 2 (array):', licenseKey1 === licenseKey2);
  console.log('Método 1 === Método 2 (buffer):', licenseKey1 === licenseKey3);
}

testWebCrypto().catch(console.error);

