plugins {
    alias(libs.plugins.nearaid.android.library)
}

android {
    namespace = "com.nearaid.core.data"
}

dependencies {
    api(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))

    implementation(libs.okhttp.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.core)
}
