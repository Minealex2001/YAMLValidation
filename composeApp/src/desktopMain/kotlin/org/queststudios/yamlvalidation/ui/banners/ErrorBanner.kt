package org.queststudios.yamlvalidation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.MaterialTheme

@Composable
fun ErrorBanner(errorBanner: String?, onDismiss: () -> Unit) {
    AnimatedVisibility(errorBanner != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    errorBanner?.contains("error", true) == true -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }
            ),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    errorBanner ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
