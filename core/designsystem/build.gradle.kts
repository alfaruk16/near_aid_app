plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.nearaid.android.library.compose)
}

android {
    namespace = "com.nearaid.core.designsystem"

    // Run Compose accessibility tests on the JVM via Robolectric (no emulator needed).
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.compose)

    // Accessibility tests (JVM / Robolectric)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.androidx.activity.compose)
    testImplementation(libs.robolectric)
}
