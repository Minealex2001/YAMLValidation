package org.queststudios.yamlvalidation.ui.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.queststudios.yamlvalidation.i18n.Strings

@Composable
fun TrialExpiredDialog(language: String) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(Strings.get(language, "trial.expired.title")) },
        text = { Text(Strings.get(language, "trial.expired.text")) },
        confirmButton = {
            Button(onClick = { System.exit(0) }) { Text(Strings.get(language, "ok")) }
        }
    )
}
