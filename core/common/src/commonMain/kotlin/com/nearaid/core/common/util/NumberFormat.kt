package com.nearaid.core.common.util

import kotlin.math.round

/**
 * Multiplatform equivalent of `String.format("%.1f", value)` — one-decimal rounding without the
 * JVM-only `String.format`. Used for trust scores, ratings and distances in shared UI.
 */
fun formatOneDecimal(value: Double): String {
    val scaled = round(value * 10).toLong()
    return "${scaled / 10}.${scaled % 10}"
}
