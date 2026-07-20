package com.nearaid.benchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cold- and warm-startup macrobenchmark for the NearAid app.
 *
 * Runs against the app's `benchmark` variant (non-debuggable, profileable) and reports
 * `timeToInitialDisplayMs`. Execute with:
 *
 *   ./gradlew :benchmark:connectedBenchmarkAndroidTest
 *
 * with a device/emulator attached. Results land in
 * `benchmark/build/outputs/connected_android_test_additional_output/`.
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
        packageName = "com.nearaid",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = mode,
    ) {
        pressHome()
        startActivityAndWait()
    }
}
