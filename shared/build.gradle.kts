plugins {
    alias(libs.plugins.nearaid.kmp.library)
    alias(libs.plugins.skie)
}

kotlin {
    // The `Shared` umbrella framework the iosApp links against. It re-exports the shared modules
    // (models, MVI base, domain, navigation and every feature's ViewModels) so Swift can construct
    // and drive them directly.
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

            // Wiring only (not exported) — needed to assemble the Koin graph.
            implementation(project(":core:data"))
            implementation(project(":core:network"))
            implementation(project(":core:datastore"))
            implementation(project(":core:database"))

            api(libs.koin.core)
        }
    }
}

android {
    namespace = "com.nearaid.shared"
}
