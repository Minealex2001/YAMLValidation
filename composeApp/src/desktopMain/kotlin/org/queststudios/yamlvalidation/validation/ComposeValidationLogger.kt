package org.queststudios.yamlvalidation.validation

import androidx.compose.runtime.mutableStateListOf

class ComposeValidationLogger : ValidationLogger {
    val logs = mutableStateListOf<Pair<String, String>>()
    override fun log(level: String, message: String) {
        logs.add(level to message)
    }
}
