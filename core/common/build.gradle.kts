plugins {
    alias(libs.plugins.nearaid.android.library)
}

android {
    namespace = "com.nearaid.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.core)
}
