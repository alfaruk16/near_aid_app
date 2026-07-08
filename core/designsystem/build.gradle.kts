plugins {
    alias(libs.plugins.nearaid.cmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:model"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.ui)

            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.coil.compose3)
            implementation(libs.coil.network.ktor3)
        }
        // Compose accessibility tests run on the JVM via Robolectric (no emulator needed).
        getByName("androidUnitTest").dependencies {
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.ui.test.junit4)
            implementation(libs.androidx.compose.ui.test.manifest)
            implementation(libs.androidx.activity.compose)
            implementation(libs.robolectric)
            implementation(libs.junit)
        }
    }
}

android {
    namespace = "com.nearaid.core.designsystem"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.nearaid.core.designsystem.resources"
}
