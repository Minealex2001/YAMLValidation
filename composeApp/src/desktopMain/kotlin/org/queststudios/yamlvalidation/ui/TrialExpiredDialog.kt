package org.queststudios.yamlvalidation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun TrialExpiredDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Prueba expirada") },
        text = { Text("El periodo de prueba de 7 días ha expirado. Por favor, adquiere una licencia para continuar usando la aplicación.") },
        confirmButton = {
            Button(onClick = { System.exit(0) }) { Text("OK") }
        }
    )
}
