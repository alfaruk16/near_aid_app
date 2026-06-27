plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.nearaid.android.hilt)
}

android {
    namespace = "com.nearaid.core.domain"
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)
}
