package com.nearaid.core.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/** Parses the API's ISO-8601 UTC timestamps (§9.1) and renders human-friendly strings. */
object TimeFormat {

    private fun isoParser(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    fun parseEpochMillis(iso: String?): Long? {
        if (iso.isNullOrBlank()) return null
        val cleaned = iso.removeSuffix("Z").substringBefore('.')
        return runCatching { isoParser().parse(cleaned)?.time }.getOrNull()
    }

    /** "18 min ago", "just now", "3 d", etc. */
    fun relativeFromNow(iso: String?, now: Long = System.currentTimeMillis()): String {
        val millis = parseEpochMillis(iso) ?: return ""
        val diff = now - millis
        if (diff < TimeUnit.MINUTES.toMillis(1)) return "just now"
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        if (minutes < 60) return "$minutes min ago"
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        if (hours < 24) return if (hours == 1L) "1 hr ago" else "$hours hr ago"
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        if (days < 7) return if (days == 1L) "1 d" else "$days d"
        return formatDate(millis)
    }

    /** "8:00 PM" — used for offer availability windows. */
    fun timeOfDay(iso: String?): String {
        val millis = parseEpochMillis(iso) ?: return ""
        return SimpleDateFormat("h:mm a", Locale.US).format(Date(millis))
    }

    fun formatDate(millis: Long): String =
        SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(millis))
}
