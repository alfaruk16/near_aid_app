plugins {
    alias(libs.plugins.nearaid.android.feature)
}

android {
    namespace = "com.nearaid.feature.activity"
}

dependencies {
    implementation(project(":core:proximity"))
}
