import java.util.Properties

plugins {
    alias(libs.plugins.nearaid.android.application)
    alias(libs.plugins.nearaid.android.application.compose)
}

// Release signing material. Read from keystore.properties (local, gitignored)
// first, then from environment variables (CI secrets). If nothing is provided
// the release build stays debug-signed, so debug/dev flows never break.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}
val releaseStoreFile = (keystoreProperties.getProperty("storeFile")
    ?: System.getenv("KEYSTORE_FILE"))?.let { rootProject.file(it) }
val releaseStorePassword = keystoreProperties.getProperty("storePassword")
    ?: System.getenv("KEYSTORE_PASSWORD")
val releaseKeyAlias = keystoreProperties.getProperty("keyAlias")
    ?: System.getenv("KEY_ALIAS")
val releaseKeyPassword = keystoreProperties.getProperty("keyPassword")
    ?: System.getenv("KEY_PASSWORD")
val hasReleaseSigning = releaseStoreFile?.exists() == true &&
    releaseStorePassword != null && releaseKeyAlias != null && releaseKeyPassword != null

android {
    namespace = "com.nearaid"

    defaultConfig {
        applicationId = "com.nearaid"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = releaseStoreFile
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        debug {
            // Local dev backend. Use 10.0.2.2 for the Android emulator (host loopback), or the
            // dev machine's LAN IP to run on a physical device on the same Wi-Fi. NOTE: the LAN IP
            // is machine/DHCP-specific — do not commit it. The backend must also bind to 0.0.0.0
            // (not 127.0.0.1) for a physical device to reach it.
            buildConfigField("String", "BASE_URL", "\"http://192.168.68.101:8000/v1/\"")
            buildConfigField("String", "WS_URL", "\"ws://192.168.68.101:8000/ws\"")
        }
        release {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://api.nearaid.app/v1/\"")
            buildConfigField("String", "WS_URL", "\"wss://api.nearaid.app/ws\"")
        }
        // Macrobenchmark target: a non-debuggable, profileable variant the :benchmark
        // module drives via `connectedBenchmarkAndroidTest`. Based on release so every
        // library module resolves its `release` variant (via matchingFallbacks) — the
        // graph has no `benchmark` variant elsewhere. Debug-signed so it installs without
        // the release keystore; minify off to keep the benchmark build fast and avoid R8
        // keep-rule risk. `isProfileable = true` injects <profileable> for Macrobenchmark.
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            isShrinkResources = false
            isProfileable = true
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:proximity"))

    // Shared Compose UI + Koin wiring (hosts the whole app tree; also drives iOS)
    implementation(project(":shared"))

    // Features
    implementation(project(":feature:auth"))
    implementation(project(":feature:discovery"))
    implementation(project(":feature:post"))
    implementation(project(":feature:activity"))
    implementation(project(":feature:messages"))
    implementation(project(":feature:profile"))

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.serialization.json)

    // Push
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // Debug tooling — auto-installs a leak-watcher in debug builds; no code changes needed
    debugImplementation(libs.leakcanary.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
