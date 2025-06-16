package org.queststudios.yamlvalidation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import org.queststudios.yamlvalidation.validation.ComposeValidationLogger
import org.queststudios.yamlvalidation.core.ValidatorCore
import org.queststudios.yamlvalidation.i18n.Strings
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow

@Composable
fun MainCard(
    yamlPath: String,
    onYamlPathChange: (String) -> Unit,
    onYamlChooser: () -> Unit,
    language: String,
    logger: ComposeValidationLogger,
    validator: ValidatorCore,
    onValidate: () -> Unit,
    onSpectral: () -> Unit,
    onExport: () -> Unit,
    onConfig: () -> Unit,
    spectralPath: String,
    showYamlChooser: Boolean,
    showSpectralChooser: Boolean,
    showExportChooser: Boolean,
    Strings: Strings
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                Strings.get(language, "app.title"),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = yamlPath,
                onValueChange = onYamlPathChange,
                label = { Text(Strings.get(language, "file.label"), color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                trailingIcon = {
                    Button(
                        onClick = onYamlChooser,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) { Text(Strings.get(language, "file.open"), color = MaterialTheme.colorScheme.onSecondaryContainer) }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onValidate,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(Strings.get(language, "validate.button"), color = MaterialTheme.colorScheme.onPrimary)
                }
                Button(
                    onClick = onSpectral,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(Strings.get(language, "export.spectral"), color = MaterialTheme.colorScheme.onTertiary)
                }
                Button(
                    onClick = onExport,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(Strings.get(language, "output.export"), color = MaterialTheme.colorScheme.onSecondary)
                }
                Button(
                    onClick = onConfig,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(Strings.get(language, "config.open"), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
