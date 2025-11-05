// Script Node.js para generar licencias para YAML Validator
// Uso: node scripts/generate-license.js [randomParam]
// Si no se proporciona randomParam, se generará uno aleatorio

const crypto = require('crypto');

const SECRET = 'VALIDADOR-YAML-2025';

// Obtener randomParam de argumentos o generar uno aleatorio
let randomParam = process.argv[2];

if (!randomParam) {
  // Generar randomParam aleatorio (16 caracteres alfanuméricos)
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  randomParam = '';
  for (let i = 0; i < 16; i++) {
    randomParam += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  console.log(`\x1b[33mGenerando randomParam aleatorio: ${randomParam}\x1b[0m`);
}

// Calcular hash SHA-256
const input = `${SECRET}-${randomParam}`;
const hash = crypto.createHash('sha256').update(input).digest();
const hashBase64 = hash.toString('base64');

// Generar clave de licencia
const licenseKey = `${hashBase64}:${randomParam}`;

console.log('');
console.log('\x1b[32m=========================================\x1b[0m');
console.log('\x1b[32mLICENCIA GENERADA\x1b[0m');
console.log('\x1b[32m=========================================\x1b[0m');
console.log('');
console.log('\x1b[36mClave de licencia:\x1b[0m');
console.log(`\x1b[37m${licenseKey}\x1b[0m`);
console.log('');
console.log('\x1b[36mRandom Param:\x1b[0m');
console.log(`\x1b[37m${randomParam}\x1b[0m`);
console.log('');
console.log('\x1b[32m=========================================\x1b[0m');
console.log('');

// Intentar copiar al portapapeles (requiere clipboardy)
try {
  const clipboardy = require('clipboardy');
  clipboardy.writeSync(licenseKey);
  console.log('\x1b[32mClave copiada al portapapeles!\x1b[0m');
} catch (error) {
  console.log('\x1b[33mUsa Ctrl+C para copiar manualmente\x1b[0m');
}

console.log('');
console.log('\x1b[90mPara usar esta licencia, cópiala en el diálogo de activación de la aplicación.\x1b[0m');

