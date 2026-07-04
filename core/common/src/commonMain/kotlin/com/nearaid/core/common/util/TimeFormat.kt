package com.nearaid.core.common.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/** Parses the API's ISO-8601 UTC timestamps (§9.1) and renders human-friendly strings. */
object TimeFormat {

    private const val MINUTE_MS = 60_000L
    private const val HOUR_MS = 60 * MINUTE_MS
    private const val DAY_MS = 24 * HOUR_MS

    // "8:00 PM" — 12-hour clock, no leading zero on the hour.
    private val timeFormat = LocalDateTime.Format {
        amPmHour(padding = Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker("AM", "PM")
    }

    // "15 Jan 2026"
    private val dateFormat = LocalDateTime.Format {
        dayOfMonth(padding = Padding.NONE)
        char(' ')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
    }

    fun parseEpochMillis(iso: String?): Long? {
        if (iso.isNullOrBlank()) return null
        val cleaned = iso.removeSuffix("Z").substringBefore('.')
        return runCatching {
            LocalDateTime.parse(cleaned).toInstant(TimeZone.UTC).toEpochMilliseconds()
        }.getOrNull()
    }

    /** "18 min ago", "just now", "3 d", etc. */
    fun relativeFromNow(iso: String?, now: Long = Clock.System.now().toEpochMilliseconds()): String {
        val millis = parseEpochMillis(iso) ?: return ""
        val diff = now - millis
        if (diff < MINUTE_MS) return "just now"
        val minutes = diff / MINUTE_MS
        if (minutes < 60) return "$minutes min ago"
        val hours = diff / HOUR_MS
        if (hours < 24) return if (hours == 1L) "1 hr ago" else "$hours hr ago"
        val days = diff / DAY_MS
        if (days < 7) return if (days == 1L) "1 d" else "$days d"
        return formatDate(millis)
    }

    /** "8:00 PM" — used for offer availability windows. */
    fun timeOfDay(iso: String?): String {
        val millis = parseEpochMillis(iso) ?: return ""
        return timeFormat.format(millis.toLocalDateTime())
    }

    fun formatDate(millis: Long): String =
        dateFormat.format(millis.toLocalDateTime())

    private fun Long.toLocalDateTime(): LocalDateTime =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())
}
