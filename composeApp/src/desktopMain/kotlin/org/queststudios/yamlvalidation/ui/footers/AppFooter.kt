package org.queststudios.yamlvalidation.ui.footers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import org.queststudios.yamlvalidation.ui.dialogs.ChangelogDialog

@Composable
fun AppFooter() {
    var showChangelog by remember { mutableStateOf(false) }
    var changelogText by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("...") }

    // Leer la versión al iniciar el Composable
    LaunchedEffect(Unit) {
        val resource = object {}.javaClass.classLoader.getResourceAsStream("changelog.md")
        val changelog = resource?.bufferedReader()?.readText() ?: ""
        val versionRegex = Regex("## \\[(.*?)\\]")
        val match = versionRegex.find(changelog)
        version = match?.groupValues?.get(1) ?: "?"
    }

    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
        Text(
            AnnotatedString("© 2025 Quest Studios | v$version"),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            modifier = Modifier
                .clickable {
                    // Leer changelog.md como recurso
                    val resource = object {}.javaClass.classLoader.getResourceAsStream("changelog.md")
                    changelogText = resource?.bufferedReader()?.readText() ?: "No se encontró changelog.md"
                    showChangelog = true
                }
                .padding(4.dp),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
    }
    if (showChangelog) {
        ChangelogDialog(
            changelog = changelogText,
            onDismiss = { }
        )
    }
}
