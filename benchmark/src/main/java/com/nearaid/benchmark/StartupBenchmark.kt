package com.nearaid.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cold- and warm-startup macrobenchmark for the NearAid app, reporting `timeToInitialDisplayMs`.
 *
 * Runs against the app's non-debuggable, profileable `benchmark` variant. Execute with a device or
 * emulator attached:
 *
 *   ./gradlew :benchmark:connectedBenchmarkAndroidTest
 *
 * Results land in `benchmark/build/outputs/connected_android_test_additional_output/`.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCold() = measure(StartupMode.COLD)

    @Test
    fun startupWarm() = measure(StartupMode.WARM)

    private fun measure(mode: StartupMode) = rule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = mode,
        // No baseline-profile compilation — keeps the run off the profileinstaller path (the only
        // local emulator is API 37, unsupported by profileinstaller 1.3.x). JIT-only baseline.
        compilationMode = CompilationMode.None(),
    ) {
        pressHome()
        startActivityAndWait()
    }

    private companion object {
        const val PACKAGE_NAME = "com.nearaid"
    }
}
