plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.nearaid.android.hilt)
}

android {
    namespace = "com.nearaid.core.ai"
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.mediapipe.tasks.text)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
