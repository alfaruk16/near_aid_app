plugins {
    alias(libs.plugins.nearaid.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // api: these types appear in this module's public API (MviViewModel : ViewModel,
            // StateFlow/Flow, and the public `commonModule` of Koin's Module type).
            api(libs.koin.core)
            api(libs.kotlinx.coroutines.core)
            api(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        getByName("androidUnitTest").dependencies {
            implementation(libs.junit)
        }
    }
}

android {
    namespace = "com.nearaid.core.common"
}
