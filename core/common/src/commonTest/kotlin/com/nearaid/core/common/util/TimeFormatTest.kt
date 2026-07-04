package com.nearaid.core.common.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimeFormatTest {

    // A known, valid ISO-8601 UTC timestamp used as the anchor for relative-time tests.
    private val iso = "2026-01-15T10:00:00"
    private val base = TimeFormat.parseEpochMillis(iso)!!

    private val minute = 60_000L
    private val hour = 60 * minute
    private val day = 24 * hour

    private fun relative(offset: Long) = TimeFormat.relativeFromNow(iso, now = base + offset)

    @Test
    fun parseEpochMillis_returns_null_for_null_blank_or_invalid_input() {
        assertNull(TimeFormat.parseEpochMillis(null))
        assertNull(TimeFormat.parseEpochMillis(""))
        assertNull(TimeFormat.parseEpochMillis("   "))
        assertNull(TimeFormat.parseEpochMillis("not-a-date"))
    }

    @Test
    fun parseEpochMillis_tolerates_a_trailing_Z_and_fractional_seconds() {
        val plain = TimeFormat.parseEpochMillis("2026-01-15T10:00:00")
        assertEquals(plain, TimeFormat.parseEpochMillis("2026-01-15T10:00:00Z"))
        assertEquals(plain, TimeFormat.parseEpochMillis("2026-01-15T10:00:00.123Z"))
    }

    @Test
    fun relativeFromNow_returns_empty_string_when_the_timestamp_cannot_be_parsed() {
        assertEquals("", TimeFormat.relativeFromNow(null))
        assertEquals("", TimeFormat.relativeFromNow("garbage"))
    }

    @Test
    fun relativeFromNow_reports_just_now_for_under_a_minute() {
        assertEquals("just now", relative(30 * 1_000L))
    }

    @Test
    fun relativeFromNow_reports_minutes() {
        assertEquals("18 min ago", relative(18 * minute))
    }

    @Test
    fun relativeFromNow_singularises_one_hour() {
        assertEquals("1 hr ago", relative(hour))
    }

    @Test
    fun relativeFromNow_reports_multiple_hours() {
        assertEquals("5 hr ago", relative(5 * hour))
    }

    @Test
    fun relativeFromNow_singularises_one_day() {
        assertEquals("1 d", relative(day))
    }

    @Test
    fun relativeFromNow_reports_multiple_days_under_a_week() {
        assertEquals("3 d", relative(3 * day))
    }

    @Test
    fun relativeFromNow_falls_back_to_an_absolute_date_beyond_a_week() {
        val result = relative(10 * day)
        assertTrue(result.matches(Regex("""\d{1,2} \w{3} \d{4}""")), "Unexpected date format: $result")
    }

    @Test
    fun timeOfDay_is_empty_for_unparseable_input_and_formatted_otherwise() {
        assertEquals("", TimeFormat.timeOfDay(null))
        val result = TimeFormat.timeOfDay(iso)
        assertTrue(result.matches(Regex("""\d{1,2}:\d{2} (AM|PM)""")), "Unexpected time format: $result")
    }

    @Test
    fun formatDate_produces_a_day_month_year_string() {
        val result = TimeFormat.formatDate(base)
        assertTrue(result.matches(Regex("""\d{1,2} \w{3} \d{4}""")), "Unexpected date format: $result")
    }
}
