package org.queststudios.yamlvalidation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp

@Composable
fun ErrorBanner(errorBanner: String?, onDismiss: () -> Unit) {
    AnimatedVisibility(errorBanner != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (errorBanner?.contains("error", true) == true) Color(0xFFFF5555) else Color(0xFF50DC78)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(errorBanner ?: "", color = Color.White, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF393E46))) {
                    Text("OK", color = Color.White)
                }
            }
        }
    }
}
