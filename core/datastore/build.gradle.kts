plugins {
    alias(libs.plugins.nearaid.android.library)
    alias(libs.plugins.nearaid.android.hilt)
}

android {
    namespace = "com.nearaid.core.datastore"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
