plugins {
    alias(libs.plugins.nearaid.android.application)
    alias(libs.plugins.nearaid.android.application.compose)
    alias(libs.plugins.nearaid.android.hilt)
}

android {
    namespace = "com.nearaid"

    defaultConfig {
        applicationId = "com.nearaid"
    }

    buildTypes {
        debug {
            // Android emulator -> host machine loopback. Change the port if your
            // local backend isn't on 8000 (Django default).
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/v1/\"")
            buildConfigField("String", "WS_URL", "\"ws://10.0.2.2:8000/ws\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://api.nearaid.app/v1/\"")
            buildConfigField("String", "WS_URL", "\"wss://api.nearaid.app/ws\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    // Features
    implementation(project(":feature:auth"))
    implementation(project(":feature:discovery"))
    implementation(project(":feature:post"))
    implementation(project(":feature:activity"))
    implementation(project(":feature:messages"))
    implementation(project(":feature:profile"))

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.kotlinx.serialization.json)

    // Push
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
