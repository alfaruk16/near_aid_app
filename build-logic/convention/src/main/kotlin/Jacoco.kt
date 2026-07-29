import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Per-module JaCoCo coverage over the debug **unit** tests. Applied by the Android library and
 * application convention plugins so every module exposes a `jacocoTestReport` task (XML + HTML).
 *
 * Non-logic bytecode is excluded so the figure reflects the testable surface (ViewModels,
 * repositories, use cases, mappers, utilities): generated Hilt/Dagger, Room `*_Impl`, Compose
 * singletons, serializer stubs, DI wiring, `R`/`BuildConfig`/`Manifest`.
 */
internal fun Project.configureJacoco() {
    pluginManager.apply("jacoco")

    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.12"
    }

    // Emit exec data for every unit-test task (e.g. testDebugUnitTest -> build/jacoco/*.exec).
    tasks.withType<Test>().configureEach {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    // Logic-coverage filter: report line coverage over hand-written *logic* (ViewModels,
    // repositories, use cases, mappers, interceptors, utilities), excluding code that isn't
    // meaningfully unit-testable — Compose UI, generated code, DI, wire DTOs/entities, nav markers.
    val coverageExclusions = listOf(
        // Android / build generated
        "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        // Hilt / Dagger generated + DI wiring
        "**/*_Hilt*.*", "**/Hilt_*.*", "**/*_Factory.*", "**/*_MembersInjector.*",
        "**/Dagger*.*", "**/*Module.*", "**/*Module_*.*", "**/hilt_aggregated_deps/**",
        "**/di/**",
        // Room generated + entities/DAOs (mapping logic is covered in :core:data)
        "**/*_Impl.*", "**/*_Impl\$*.*", "**/entity/**", "**/dao/**", "**/*Database*.*",
        // kotlinx.serialization generated + wire DTOs (pure data holders)
        "**/*\$\$serializer.*", "**/dto/**",
        // Compose UI — screens, components, theme, previews, singletons (needs instrumented tests)
        "**/ComposableSingletons*.*", "**/*Screen*.*", "**/*ScreenKt*.*",
        "**/theme/**", "**/component/**", "**/*Preview*.*", "**/*Kt\$*Preview*.*",
        // Navigation route markers + Android framework entry points (Activities, Application, the
        // FCM service). Precise patterns so `feature:activity`'s *ActivityViewModel etc. still count.
        "**/navigation/**", "**/*NavHost*.*", "**/*Destination*.*",
        "**/*Application.*", "**/*Activity.*", "**/MainActivity*.*", "**/*MessagingService*.*",
    )

    tasks.register("jacocoTestReport", JacocoReport::class.java) {
        dependsOn("testDebugUnitTest")
        group = "verification"
        description = "Generates JaCoCo line/branch coverage for the debug unit tests."
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
        val buildDir = layout.buildDirectory.get().asFile
        classDirectories.setFrom(
            files(
                fileTree("$buildDir/tmp/kotlin-classes/debug") { exclude(coverageExclusions) },
                fileTree("$buildDir/intermediates/javac/debug/classes") { exclude(coverageExclusions) },
            ),
        )
        sourceDirectories.setFrom(files("src/main/kotlin", "src/main/java"))
        executionData.setFrom(fileTree(buildDir) { include("jacoco/testDebugUnitTest.exec") })
    }
}
