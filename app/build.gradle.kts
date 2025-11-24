import java.security.KeyStore
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
    id("org.jetbrains.kotlinx.kover")
    id("org.owasp.dependencycheck")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun credential(key: String): String? = keystoreProperties.getProperty(key) ?: localProperties.getProperty(key) ?: System.getenv(key)

val releaseSigningCredentialKeys = listOf(
    "RELEASE_STORE_PASSWORD",
    "RELEASE_KEY_ALIAS",
    "RELEASE_KEY_PASSWORD"
)

val releaseSigningCredentials = releaseSigningCredentialKeys.associateWith { credential(it) }
val releaseKeystoreFile = rootProject.file("keystore/release_new.keystore")
val releaseSigningConfigured = releaseSigningCredentials.values.all { !it.isNullOrBlank() }
val releaseSigningReady = if (releaseSigningConfigured && releaseKeystoreFile.exists()) {
    val storePassword = releaseSigningCredentials["RELEASE_STORE_PASSWORD"]!!.toCharArray()
    val keyAlias = releaseSigningCredentials["RELEASE_KEY_ALIAS"]!!
    val keyPassword = releaseSigningCredentials["RELEASE_KEY_PASSWORD"]!!.toCharArray()
    runCatching {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        releaseKeystoreFile.inputStream().use { keyStore.load(it, storePassword) }
        require(keyStore.containsAlias(keyAlias)) { "Alias '$keyAlias' not found in release keystore." }
        keyStore.getKey(keyAlias, keyPassword)
        true
    }.getOrElse {
        println("[NeverZero] Release signing disabled: ${it.message}")
        false
    }
} else {
    if (!releaseKeystoreFile.exists()) {
        println("[NeverZero] Release signing disabled: keystore/release_new.keystore missing.")
    }
    false
}

val geminiApiKey = credential("GEMINI_API_KEY") ?: ""

android {
    namespace = "com.productivitystreak"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.productivitystreak"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$geminiApiKey\""
        )
    }

    signingConfigs {
        create("release") {
            storeFile = releaseKeystoreFile
            storePassword = releaseSigningCredentials["RELEASE_STORE_PASSWORD"]
            keyAlias = releaseSigningCredentials["RELEASE_KEY_ALIAS"]
            keyPassword = releaseSigningCredentials["RELEASE_KEY_PASSWORD"]
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        getByName("debug") {
            if (releaseSigningReady) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.2"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.08.00")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-text-google-fonts")

    implementation("androidx.navigation:navigation-compose:2.8.0")

    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.8.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("androidx.work:work-runtime-ktx:2.9.1")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")
}

kover {
    useJacoco()
}