plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.nearaid.android.library.compose)
}

android {
    namespace = "com.nearaid.core.designsystem"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.compose)
}
