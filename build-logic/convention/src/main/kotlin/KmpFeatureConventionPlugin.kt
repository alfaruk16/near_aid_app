import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convention for a KMP feature module. The shared MVI surface — `*Contract.kt`, `*ViewModel.kt`
 * and the feature's Koin `module {}` — lives in `commonMain` and compiles for Android + iOS; the
 * Android Compose `Screen.kt` composables stay in `androidMain`. Compose is enabled on the Android
 * target only (Phase 3 is logic-only KMM; Compose Multiplatform is a later, optional phase).
 *
 * Each feature declares its own dependencies in `kotlin { sourceSets { ... } }` and sets
 * `android { namespace = "..." }`.
 */
class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                androidTarget {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.fromTarget(NearAidBuildConfig.JAVA_VERSION.toString()))
                    }
                }
                iosX64()
                iosArm64()
                iosSimulatorArm64()
            }

            extensions.configure<LibraryExtension> {
                compileSdk = NearAidBuildConfig.COMPILE_SDK
                defaultConfig {
                    minSdk = NearAidBuildConfig.MIN_SDK
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion(NearAidBuildConfig.JAVA_VERSION)
                    targetCompatibility = JavaVersion.toVersion(NearAidBuildConfig.JAVA_VERSION)
                }
                buildFeatures {
                    compose = true
                }
            }
        }
    }
}
