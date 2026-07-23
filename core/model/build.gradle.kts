plugins {
    alias(libs.plugins.nearaid.kmp.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    // The `Shared` iOS framework now lives in `:shared` — the umbrella that re-exports every shared
    // module. `:core:model` is just one more module folded into it (Phase 0 produced the framework
    // here as the walking skeleton; Phase 3's milestone moved it to the umbrella).
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.nearaid.core.model"
}
