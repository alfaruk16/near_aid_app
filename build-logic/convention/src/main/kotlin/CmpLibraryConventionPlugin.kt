import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convention for a **Compose Multiplatform** UI module — the design system and every feature's
 * `Screen.kt`s live in `commonMain` and render on Android + iOS from one codebase. Applies the
 * JetBrains Compose plugin (artifacts + `composeResources`) alongside the Kotlin Compose compiler.
 *
 * Modules declare their own `compose.*` / feature dependencies and set `android { namespace }`.
 */
class CmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
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
            }
        }
    }
}
