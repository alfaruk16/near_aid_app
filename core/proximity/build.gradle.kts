plugins {
    alias(libs.plugins.nearaid.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // api: ProximityConfirmer is bound into the Koin graph, exposed via `proximityModule`.
            api(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        getByName("androidUnitTest").dependencies {
            implementation(libs.junit)
            implementation(libs.mockk)
            implementation(libs.kotlinx.coroutines.test)
        }
        // On-device hardware proof for the BLE handoff — no radio in the emulator/unit tests,
        // so these run via `connectedAndroidTest` against real phones. See scripts/ble-proximity-proof.sh.
        getByName("androidInstrumentedTest").dependencies {
            implementation(libs.androidx.junit)
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.test.rules)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.nearaid.core.proximity"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
