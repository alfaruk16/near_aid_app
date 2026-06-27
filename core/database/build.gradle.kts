plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.nearaid.android.hilt)
    alias(libs.plugins.nearaid.android.room)
}

android {
    namespace = "com.nearaid.core.database"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
