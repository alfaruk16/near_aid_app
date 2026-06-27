package com.nearaid.core.domain.usecase

/** Bangladesh-first phone handling — normalises local input to E.164 (§4.1, FR-1). */
object PhoneNumber {
    private const val BD_PREFIX = "+880"

    /** Returns an E.164 string, or null if the input cannot be a valid BD mobile number. */
    fun normalizeBd(raw: String): String? {
        val digits = raw.filter { it.isDigit() }
        val national = when {
            digits.startsWith("880") -> digits.removePrefix("880")
            digits.startsWith("0") -> digits.removePrefix("0")
            else -> digits
        }
        // BD mobile numbers are 10 national digits starting with 1 (e.g. 1712345678).
        if (national.length != 10 || !national.startsWith("1")) return null
        return BD_PREFIX + national
    }

    fun isValidBd(raw: String): Boolean = normalizeBd(raw) != null

    /** "+8801712345678" -> "+880 1712 345 678" for display. */
    fun formatForDisplay(e164: String): String {
        val national = e164.removePrefix(BD_PREFIX)
        if (national.length != 10) return e164
        return "$BD_PREFIX ${national.substring(0, 4)} ${national.substring(4, 7)} ${national.substring(7)}"
    }
}
