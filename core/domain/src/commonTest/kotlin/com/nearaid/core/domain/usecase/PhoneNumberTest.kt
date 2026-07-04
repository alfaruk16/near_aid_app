package com.nearaid.core.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhoneNumberTest {

    private val e164 = "+8801712345678"

    @Test
    fun normalizeBd_accepts_a_leading_zero_local_number() {
        assertEquals(e164, PhoneNumber.normalizeBd("01712345678"))
    }

    @Test
    fun normalizeBd_accepts_a_bare_10_digit_national_number() {
        assertEquals(e164, PhoneNumber.normalizeBd("1712345678"))
    }

    @Test
    fun normalizeBd_accepts_a_number_with_the_880_country_code() {
        assertEquals(e164, PhoneNumber.normalizeBd("8801712345678"))
    }

    @Test
    fun normalizeBd_strips_spaces_dashes_and_a_plus_sign() {
        assertEquals(e164, PhoneNumber.normalizeBd("+880 1712-345 678"))
    }

    @Test
    fun normalizeBd_rejects_numbers_that_are_too_short() {
        assertNull(PhoneNumber.normalizeBd("012345"))
    }

    @Test
    fun normalizeBd_rejects_national_numbers_not_starting_with_1() {
        assertNull(PhoneNumber.normalizeBd("02123456789"))
    }

    @Test
    fun normalizeBd_rejects_blank_input() {
        assertNull(PhoneNumber.normalizeBd(""))
        assertNull(PhoneNumber.normalizeBd("   "))
    }

    @Test
    fun isValidBd_mirrors_normalizeBd() {
        assertTrue(PhoneNumber.isValidBd("01712345678"))
        assertFalse(PhoneNumber.isValidBd("12345"))
    }

    @Test
    fun formatForDisplay_groups_a_valid_E164_number() {
        assertEquals("+880 1712 345 678", PhoneNumber.formatForDisplay(e164))
    }

    @Test
    fun formatForDisplay_returns_non_standard_input_unchanged() {
        assertEquals("+88012345", PhoneNumber.formatForDisplay("+88012345"))
    }
}
