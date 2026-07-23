package com.nearaid.core.common.util

import kotlin.test.Test
import kotlin.test.assertEquals

class NumberFormatTest {

    @Test
    fun formats_a_whole_number_with_one_decimal() {
        assertEquals("4.0", formatOneDecimal(4.0))
        assertEquals("0.0", formatOneDecimal(0.0))
    }

    @Test
    fun rounds_down_when_the_second_decimal_is_below_five() {
        assertEquals("4.2", formatOneDecimal(4.23))
        assertEquals("12.7", formatOneDecimal(12.74))
    }

    @Test
    fun rounds_up_when_the_second_decimal_is_above_five() {
        assertEquals("4.3", formatOneDecimal(4.28))
        assertEquals("3.9", formatOneDecimal(3.87))
    }

    @Test
    fun carries_over_to_the_next_integer() {
        assertEquals("5.0", formatOneDecimal(4.99))
        assertEquals("10.0", formatOneDecimal(9.96))
    }

    @Test
    fun formats_a_typical_trust_score() {
        assertEquals("4.2", formatOneDecimal(4.23))
    }
}
