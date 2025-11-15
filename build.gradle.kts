plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("org.jetbrains.kotlinx.kover") version "0.8.0" apply false
    id("org.owasp.dependencycheck") version "9.0.0" apply false
}

// Add kover plugin to all projects
subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
}