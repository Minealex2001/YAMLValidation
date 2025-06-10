package org.queststudios.yamlvalidation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.util.prefs.Preferences
import org.queststudios.yamlvalidation.ui.screens.AppContent

fun main() = application {
    val prefs = Preferences.userRoot().node("org.queststudios.yamlvalidation.window")
    val wasMaximized = prefs.getBoolean("maximized", true)
    val width = prefs.getInt("width", 1600)
    val height = prefs.getInt("height", 1200)
    val posX = prefs.getInt("posX", -1)
    val posY = prefs.getInt("posY", -1)

    val windowState = rememberWindowState(
        width = width.dp,
        height = height.dp,
        position = if (posX >= 0 && posY >= 0) WindowPosition(x = posX.dp, y = posY.dp) else WindowPosition.Aligned(Alignment.Center),
        placement = if (wasMaximized) WindowPlacement.Maximized else WindowPlacement.Floating
    )

    Window(
        onCloseRequest = {
            // Guardar estado al cerrar
            prefs.putBoolean("maximized", windowState.placement == WindowPlacement.Maximized)
            prefs.putInt("width", windowState.size.width.value.toInt())
            prefs.putInt("height", windowState.size.height.value.toInt())
            prefs.putInt("posX", windowState.position.x.value.toInt())
            prefs.putInt("posY", windowState.position.y.value.toInt())
            exitApplication()
        },
        title = "Validador YAML",
        state = windowState,
        resizable = true
    ) {
        this.window.minimumSize = java.awt.Dimension(1400, 900)
        Box(Modifier.fillMaxSize()) {
            AppContent()
        }
    }
}
