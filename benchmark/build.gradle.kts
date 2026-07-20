plugins {
    // AGP + Kotlin are already on the build classpath (declared `apply false` at the root),
    // so these are applied by id without a version to avoid a version-conflict error.
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.nearaid.benchmark"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Only run against the app's non-debuggable, profileable `benchmark` variant.
    buildTypes {
        create("benchmark") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            // Resolve the app's `benchmark` variant; its deps fall back to `release`.
            matchingFallbacks += listOf("release")
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.uiautomator)
}

androidComponents {
    beforeVariants(selector().all()) {
        // Macrobenchmark only makes sense against the `benchmark` build type.
        it.enable = it.buildType == "benchmark"
    }
}
