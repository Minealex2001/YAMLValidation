package org.queststudios.yamlvalidation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp

@Composable
fun LicenseDialog(
    licenseInput: String,
    onLicenseInputChange: (String) -> Unit,
    licenseError: String?,
    onActivate: () -> Unit,
    onTrial: (() -> Unit)? = null,
    trialActive: Boolean = false,
    daysLeft: Int = 0,
    trialUsed: Boolean = false
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Activación de licencia") },
        text = {
            Column {
                Text("Introduce tu clave de licencia para activar el Validador YAML.")
                OutlinedTextField(
                    value = licenseInput,
                    onValueChange = onLicenseInputChange,
                    label = { Text("Clave de licencia") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (licenseError != null) Text(licenseError, color = Color.Red)
                Spacer(Modifier.height(12.dp))
                if (!trialActive && onTrial != null) {
                    Button(
                        onClick = onTrial,
                        enabled = !trialUsed,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("No tengo licencia (Prueba 7 días)") }
                } else if (trialActive) {
                    Text("Prueba activa: $daysLeft días restantes", color = Color(0xFF00ADB5), fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(onClick = onActivate) { Text("Activar") }
        }
    )
}
