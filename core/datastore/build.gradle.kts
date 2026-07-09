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
    }
}

android {
    namespace = "com.nearaid.core.datastore"
}
