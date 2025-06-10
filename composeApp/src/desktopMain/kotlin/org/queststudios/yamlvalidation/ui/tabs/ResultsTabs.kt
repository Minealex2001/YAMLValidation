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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF23272F))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { onTabChange(0) }, text = { Text(Strings.get(language, "tab.appValidations")) })
                Tab(selected = selectedTab == 1, onClick = { onTabChange(1) }, text = { Text(Strings.get(language, "tab.spectralValidations")) })
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.heightIn(min = 200.dp, max = 320.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                when (selectedTab) {
                    0 -> Column(Modifier.fillMaxWidth()) {
                        logger.logs.forEach { (level, msg) ->
                            val color = when (level.uppercase()) {
                                "ERROR" -> Color(0xFFFF5555)
                                "WARNING", "WARN" -> Color(0xFFFFB43C)
                                "SUCCESS", "OK" -> Color(0xFF50DC78)
                                "INFO" -> Color(0xFF00ADB5)
                                else -> Color(0xFFECECEC)
                            }
                            Text("[$level] $msg", color = color)
                        }
                    }
                    1 -> Column(Modifier.fillMaxWidth()) {
                        if (spectralOutput.isNotBlank()) {
                            Text(spectralOutput, color = Color(0xFFB0B0B0))
                        }
                    }
                }
            }
        }
    }
}
