import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/**
 * Convention for a feature module: an Android library + Compose + Hilt, with the standard
 * navigation / lifecycle deps and the core modules every feature needs already wired.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("nearaid.android.library")
                apply("nearaid.android.library.compose")
                apply("nearaid.android.hilt")
            }

            dependencies {
                projectImpl(":core:common")
                projectImpl(":core:model")
                projectImpl(":core:domain")
                projectImpl(":core:designsystem")
                projectImpl(":core:navigation")

                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("androidx-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
                add("implementation", libs.findLibrary("kotlinx-serialization-json").get())
                add("implementation", libs.findLibrary("coil-compose").get())
            }
        }
    }
}

private fun DependencyHandler.projectImpl(path: String) {
    add("implementation", project(path))
}
