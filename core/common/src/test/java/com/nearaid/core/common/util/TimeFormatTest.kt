package com.nearaid.core.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeFormatTest {

    // A known, valid ISO-8601 UTC timestamp used as the anchor for relative-time tests.
    private val iso = "2026-01-15T10:00:00"
    private val base = TimeFormat.parseEpochMillis(iso)!!

    private fun relative(offset: Long) = TimeFormat.relativeFromNow(iso, now = base + offset)

    @Test
    fun `parseEpochMillis returns null for null blank or invalid input`() {
        assertNull(TimeFormat.parseEpochMillis(null))
        assertNull(TimeFormat.parseEpochMillis(""))
        assertNull(TimeFormat.parseEpochMillis("   "))
        assertNull(TimeFormat.parseEpochMillis("not-a-date"))
    }

    @Test
    fun `parseEpochMillis tolerates a trailing Z and fractional seconds`() {
        val plain = TimeFormat.parseEpochMillis("2026-01-15T10:00:00")
        assertEquals(plain, TimeFormat.parseEpochMillis("2026-01-15T10:00:00Z"))
        assertEquals(plain, TimeFormat.parseEpochMillis("2026-01-15T10:00:00.123Z"))
    }

    @Test
    fun `relativeFromNow returns empty string when the timestamp cannot be parsed`() {
        assertEquals("", TimeFormat.relativeFromNow(null))
        assertEquals("", TimeFormat.relativeFromNow("garbage"))
    }

    @Test
    fun `relativeFromNow reports just now for under a minute`() {
        assertEquals("just now", relative(TimeUnit.SECONDS.toMillis(30)))
    }

    @Test
    fun `relativeFromNow reports minutes`() {
        assertEquals("18 min ago", relative(TimeUnit.MINUTES.toMillis(18)))
    }

    @Test
    fun `relativeFromNow singularises one hour`() {
        assertEquals("1 hr ago", relative(TimeUnit.HOURS.toMillis(1)))
    }

    @Test
    fun `relativeFromNow reports multiple hours`() {
        assertEquals("5 hr ago", relative(TimeUnit.HOURS.toMillis(5)))
    }

    @Test
    fun `relativeFromNow singularises one day`() {
        assertEquals("1 d", relative(TimeUnit.DAYS.toMillis(1)))
    }

    @Test
    fun `relativeFromNow reports multiple days under a week`() {
        assertEquals("3 d", relative(TimeUnit.DAYS.toMillis(3)))
    }

    @Test
    fun `relativeFromNow falls back to an absolute date beyond a week`() {
        // Formatting uses the default timezone, so assert the shape rather than the exact value.
        val result = relative(TimeUnit.DAYS.toMillis(10))
        assertTrue("Unexpected date format: $result", result.matches(Regex("""\d{1,2} \w{3} \d{4}""")))
    }

    @Test
    fun `timeOfDay is empty for unparseable input and formatted otherwise`() {
        assertEquals("", TimeFormat.timeOfDay(null))
        val result = TimeFormat.timeOfDay(iso)
        assertTrue("Unexpected time format: $result", result.matches(Regex("""\d{1,2}:\d{2} (AM|PM)""")))
    }

    @Test
    fun `formatDate produces a day-month-year string`() {
        val result = TimeFormat.formatDate(base)
        assertTrue("Unexpected date format: $result", result.matches(Regex("""\d{1,2} \w{3} \d{4}""")))
    }
}
