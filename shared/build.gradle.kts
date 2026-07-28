plugins {
    alias(libs.plugins.nearaid.cmp.library)
    alias(libs.plugins.skie)
}

kotlin {
    // The `Shared` umbrella framework the iosApp links against. It re-exports the shared modules
    // (models, MVI base, domain, navigation and every feature's ViewModels) so Swift can construct
    // and drive them directly, and now hosts the shared Compose UI via `MainViewController`.
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(project(":core:model"))
            export(project(":core:common"))
            export(project(":core:domain"))
            export(project(":core:navigation"))
            export(project(":feature:auth"))
            export(project(":feature:discovery"))
            export(project(":feature:post"))
            export(project(":feature:activity"))
            export(project(":feature:messages"))
            export(project(":feature:profile"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Exported (api) — types Swift needs to see.
            api(project(":core:model"))
            api(project(":core:common"))
            api(project(":core:domain"))
            api(project(":core:navigation"))
            api(project(":feature:auth"))
            api(project(":feature:discovery"))
            api(project(":feature:post"))
            api(project(":feature:activity"))
            api(project(":feature:messages"))
            api(project(":feature:profile"))

            // Wiring only (not exported) — needed to assemble the Koin graph + shared UI.
            implementation(project(":core:data"))
            implementation(project(":core:network"))
            implementation(project(":core:datastore"))
            implementation(project(":core:database"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:proximity"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.ui)

            // Coil 3 — a singleton ImageLoader with the Ktor network fetcher is installed in App()
            // so remote images (avatars) load on both Android and iOS.
            implementation(libs.coil.compose3)
            implementation(libs.coil.network.ktor3)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)

            api(libs.koin.core)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
    }
}

android {
    namespace = "com.nearaid.shared"
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.nearaid.shared.resources"
}
