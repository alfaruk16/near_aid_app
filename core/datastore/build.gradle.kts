plugins {
    alias(libs.plugins.nearaid.android.library)
}

android {
    namespace = "com.nearaid.core.datastore"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.android)
}
