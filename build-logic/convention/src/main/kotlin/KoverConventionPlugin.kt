import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Test-coverage tooling (Kotlinx Kover). Applied once at the root project; it
 * applies Kover to every subproject and wires each one into a single merged
 * report on the root so `./gradlew koverHtmlReport` / `koverXmlReport` produce
 * one aggregated coverage number across all modules.
 *
 * Coverage is gathered from the JVM/Android unit tests (the same
 * `testDebugUnitTest` graph CI already runs, which also executes commonTest via
 * the Android target). Generated and framework-only code is excluded below so
 * the percentage reflects hand-written logic.
 *
 * See docs/ci-cd.md.
 */
class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        require(this == rootProject) {
            "nearaid.kover must be applied to the root project only."
        }

        pluginManager.apply("org.jetbrains.kotlinx.kover")

        // The macrobenchmark module (com.android.test) has no coverable production code
        // and no library/app variant for Kover to instrument — keep it out of the report.
        val covered = subprojects.filter { it.path != ":benchmark" }

        subprojects {
            if (path == ":benchmark") return@subprojects
            pluginManager.apply("org.jetbrains.kotlinx.kover")

            extensions.configure<KoverProjectExtension> {
                reports {
                    filters {
                        excludes {
                            // Generated code and DI/framework glue with no branch logic worth measuring.
                            classes(
                                "*.BuildConfig",
                                "*.ComposableSingletons*",
                                "*_*Factory",
                                "*Kt\$*",
                                "*.databinding.*",
                            )
                            // Compose UI + generated resource accessors: excluded so the number
                            // reflects testable logic (ViewModels, repositories, use cases, mappers).
                            annotatedBy("androidx.compose.runtime.Composable")
                        }
                    }
                }
            }
        }

        // Merge every covered subproject into the root report.
        dependencies {
            covered.forEach { add("kover", it) }
        }
    }
}
