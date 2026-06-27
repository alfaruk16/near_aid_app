import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/** Configure base Kotlin + Android options shared by application and library modules. */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = NearAidBuildConfig.COMPILE_SDK

        defaultConfig {
            minSdk = NearAidBuildConfig.MIN_SDK
        }

        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(NearAidBuildConfig.JAVA_VERSION)
            targetCompatibility = JavaVersion.toVersion(NearAidBuildConfig.JAVA_VERSION)
        }
    }

    configureKotlin()
}

/** Configure base Kotlin options for a plain JVM (non-Android) module. */
internal fun Project.configureKotlinJvm() {
    extensions.configure(org.gradle.api.plugins.JavaPluginExtension::class.java) {
        sourceCompatibility = JavaVersion.toVersion(NearAidBuildConfig.JAVA_VERSION)
        targetCompatibility = JavaVersion.toVersion(NearAidBuildConfig.JAVA_VERSION)
    }
    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(NearAidBuildConfig.JAVA_VERSION.toString()))
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
            )
        }
    }
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = NearAidBuildConfig.JAVA_VERSION.toString()
        targetCompatibility = NearAidBuildConfig.JAVA_VERSION.toString()
    }
}
