import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/** SDK / Java targets shared across every module. Change in one place only. */
object NearAidBuildConfig {
    const val COMPILE_SDK = 35
    const val MIN_SDK = 24
    const val TARGET_SDK = 35
    const val JAVA_VERSION = 17
}

/** Convenience accessor for the `libs` version catalog from inside convention plugins. */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
