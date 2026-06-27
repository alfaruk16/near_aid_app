plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nearaid.core.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
