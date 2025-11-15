plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.productivitystreak.uioverrides"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = false
    }

    configurations.all {
        exclude(group = "androidx.compose.ui", module = "ui-release")
    }
}

dependencies {
    // No runtime deps; this module only hosts resource overrides.
}
