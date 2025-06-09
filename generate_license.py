import sys
import base64
import hashlib
from datetime import date

SECRET = "VALIDADOR-YAML-2025"  # Debe coincidir con el de LicenseManager.kt

def generate_key(for_date=None):
    if for_date is None:
        for_date = date.today().isoformat()
    raw = f"{SECRET}-{for_date}"
    h = hashlib.sha256(raw.encode()).digest()
    return base64.b64encode(h).decode()

if __name__ == "__main__":
    if len(sys.argv) > 1:
        d = sys.argv[1]
    else:
        d = date.today().isoformat()
    print(generate_key(d))
