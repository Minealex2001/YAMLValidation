const crypto = require('crypto');

const SECRET_NTT = 'VALIDADOR-YAML-2025-NTTDATA';
const NTT_PREFIX = 'NTT:';

function generateNTTLicense(randomParam) {
  const input = `${SECRET_NTT}-${randomParam}`;
  const hash = crypto.createHash('sha256').update(input).digest();
  const hashArray = Array.from(new Uint8Array(hash));
  const hashBase64 = Buffer.from(hashArray.map(b => String.fromCharCode(b)).join(''), 'binary').toString('base64');
  return `${NTT_PREFIX}${hashBase64}:${randomParam}`;
}

// Generar licencia de NTT
let randomParam = process.argv[2];
if (!randomParam) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  randomParam = '';
  for (let i = 0; i < 16; i++) {
    randomParam += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  console.log(`\x1b[33mGenerando randomParam aleatorio: ${randomParam}\x1b[0m`);
}

const licenseKey = generateNTTLicense(randomParam);

console.log('');
console.log('\x1b[32m=========================================\x1b[0m');
console.log('\x1b[32mNTT DATA LICENSE GENERADA\x1b[0m');
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
console.log('\x1b[90mPara usar esta licencia, cópiala en el diálogo de activación de la aplicación.\x1b[0m');
console.log('\x1b[90mAl activarla, se mostrará el logo de NTT Data y el branding de la empresa.\x1b[0m');
console.log('');
