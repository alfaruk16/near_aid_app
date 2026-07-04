import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Convention plugin for Kotlin Multiplatform library modules that ship both an Android
 * variant and the three iOS targets. The iOS framework binary (if any) is declared in the
 * module's own build script so this plugin stays reusable across every KMP module.
 *
 * Modules must set `android { namespace = "..." }` themselves.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
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
