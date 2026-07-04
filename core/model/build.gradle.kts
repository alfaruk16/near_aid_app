plugins {
    alias(libs.plugins.nearaid.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    // Expose the shared code to Xcode as the `Shared` framework. As more modules become
    // multiplatform this framework becomes the umbrella the iosApp links against.
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

android {
    namespace = "com.nearaid.core.model"
}
