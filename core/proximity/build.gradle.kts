plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.nearaid.android.hilt)
}

android {
    namespace = "com.nearaid.core.proximity"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
