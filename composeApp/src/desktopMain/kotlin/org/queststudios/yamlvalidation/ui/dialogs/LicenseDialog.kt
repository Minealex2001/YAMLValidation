package org.queststudios.yamlvalidation.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.queststudios.yamlvalidation.i18n.Strings

@Composable
fun LicenseDialog(
    licenseInput: String,
    onLicenseInputChange: (String) -> Unit,
    licenseError: String?,
    onActivate: () -> Unit,
    onTrial: (() -> Unit)? = null,
    trialActive: Boolean = false,
    daysLeft: Int = 0,
    trialUsed: Boolean = false,
    onDismiss: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        title = { Text(Strings.get("es", "license.title")) },
        text = {
            Box(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Text(Strings.get("es", "license.intro"))
                    OutlinedTextField(
                        value = licenseInput,
                        onValueChange = onLicenseInputChange,
                        label = { Text(Strings.get("es", "license.key")) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (licenseError != null) Text(licenseError, color = Color.Red)
                    Spacer(Modifier.height(12.dp))
                    if (!trialActive && onTrial != null) {
                        Button(
                            onClick = onTrial,
                            enabled = !trialUsed,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(Strings.get("es", "license.trial")) }
                    } else if (trialActive) {
                        Text(Strings.get("es", "license.trialActive").replace("{0}", daysLeft.toString()), color = Color(0xFF00ADB5), fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onActivate) { Text(Strings.get("es", "license.activate")) }
        },
        dismissButton = {
            Button(onClick = { onDismiss?.invoke() }) {
                Text("Cerrar")
            }
        }
    )
}
