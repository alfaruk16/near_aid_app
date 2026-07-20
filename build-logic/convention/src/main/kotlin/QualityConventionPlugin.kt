import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * Phase 2 quality gates. Applied once at the root project; it configures
 * Spotless (ktlint) and detekt across the root and every subproject so all
 * modules inherit the same rules without editing 16 build scripts.
 *
 * See docs/ci-cd.md.
 */
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val ktlintVersion = extensions.getByType<VersionCatalogsExtension>()
            .named("libs")
            .findVersion("ktlint")
            .get()
            .requiredVersion

        allprojects {
            // --- Spotless (formatting) ---
            pluginManager.apply("com.diffplug.spotless")
            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("src/**/*.kt")
                    targetExclude("**/build/**/*.kt")
                    ktlint(ktlintVersion)
                    trimTrailingWhitespace()
                    endWithNewline()
                }
                kotlinGradle {
                    target("*.gradle.kts")
                    ktlint(ktlintVersion)
                }
            }

            // --- detekt (static analysis) ---
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                parallel = true
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                baseline = file("detekt-baseline.xml")
                basePath = rootProject.projectDir.path
                // Default detekt source detection only covers src/main; list the KMP
                // source sets explicitly. Missing directories are ignored.
                source.setFrom(
                    files(
                        "src/commonMain/kotlin",
                        "src/androidMain/kotlin",
                        "src/iosMain/kotlin",
                        "src/main/kotlin",
                        "src/commonTest/kotlin",
                        "src/androidUnitTest/kotlin",
                        "src/test/kotlin",
                    ),
                )
            }
            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    sarif.required.set(true)
                    xml.required.set(false)
                    txt.required.set(false)
                }
            }
        }
    }
}
