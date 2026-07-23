plugins {
    alias(libs.plugins.nearaid.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:model"))
            api(libs.androidx.datastore.preferences.core)
            implementation(libs.okio)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.security.crypto)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        getByName("androidUnitTest").dependencies {
            implementation(libs.junit)
            implementation(libs.okio)
        }
    }
}

android {
    namespace = "com.nearaid.core.datastore"
}
