plugins {
    alias(libs.plugins.nearaid.cmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:model"))
            implementation(project(":core:domain"))
            implementation(project(":core:navigation"))
            implementation(project(":core:designsystem"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.ui)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)

            implementation(libs.koin.core)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        getByName("androidUnitTest").dependencies {
            implementation(libs.junit)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.mockk)
        }
    }
}

android {
    namespace = "com.nearaid.feature.profile"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.nearaid.feature.profile.resources"
}
