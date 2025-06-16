package org.queststudios.yamlvalidation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import org.queststudios.yamlvalidation.validation.ComposeValidationLogger
import org.queststudios.yamlvalidation.i18n.Strings
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow

@Composable
fun ResultsTabs(
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    logger: ComposeValidationLogger,
    spectralOutput: String,
    language: String,
    Strings: Strings
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).shadow(8.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(selected = selectedTab == 0, onClick = { onTabChange(0) }, text = { Text(Strings.get(language, "tab.appValidations"), color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) })
                Tab(selected = selectedTab == 1, onClick = { onTabChange(1) }, text = { Text(Strings.get(language, "tab.spectralValidations"), color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) })
            }
            Spacer(Modifier.height(10.dp))
            Box(Modifier.heightIn(min = 200.dp, max = 320.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                when (selectedTab) {
                    0 -> Column(Modifier.fillMaxWidth()) {
                        logger.logs.forEach { (level, msg) ->
                            val color = when (level.uppercase()) {
                                "ERROR" -> MaterialTheme.colorScheme.error
                                "WARNING", "WARN" -> MaterialTheme.colorScheme.secondary
                                "SUCCESS", "OK" -> MaterialTheme.colorScheme.tertiary
                                "INFO" -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                            Text("[$level] $msg", color = color, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    1 -> Column(Modifier.fillMaxWidth()) {
                        if (spectralOutput.isNotBlank()) {
                            Text(spectralOutput, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
