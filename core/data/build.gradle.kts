plugins {
    alias(libs.plugins.nearaid.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:domain"))
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(project(":core:network"))
            implementation(project(":core:database"))
            implementation(project(":core:datastore"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.okio)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "com.nearaid.core.data"
}
