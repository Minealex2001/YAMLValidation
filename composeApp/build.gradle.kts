import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {

            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // YAML parser
            implementation("org.yaml:snakeyaml:2.2")
            // Compose FileDialog y recursos multiplataforma
            implementation("org.jetbrains.compose.components:components-resources-desktop:1.6.10")
            implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.6.10")
            // implementation("androidx.compose.ui:ui-text-markdown:1.6.10")
            // Markdown Compose de Mike Penz (compatible Desktop)
            //implementation("com.github.jeziellago:compose-markdown:{LAST-RELEASE}")
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.queststudios.yamlvalidation.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.queststudios.yamlvalidation"
            packageVersion = "1.0.0"
        }
    }
}
