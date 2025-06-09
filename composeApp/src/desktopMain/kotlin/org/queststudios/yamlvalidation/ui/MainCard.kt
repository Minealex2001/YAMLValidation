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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF393E46))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                Strings.get(language, "app.title"),
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF00ADB5),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = yamlPath,
                onValueChange = onYamlPathChange,
                label = { Text(Strings.get(language, "file.label")) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Button(onClick = onYamlChooser) { Text(Strings.get(language, "file.open")) }
                }
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onValidate) {
                    Text(Strings.get(language, "validate.button"))
                }
                Button(onClick = onSpectral) {
                    Text(Strings.get(language, "export.spectral"))
                }
                Button(onClick = onExport) {
                    Text(Strings.get(language, "output.export"))
                }
                Button(onClick = onConfig) {
                    Text(Strings.get(language, "config.open"))
                }
            }
        }
    }
}
