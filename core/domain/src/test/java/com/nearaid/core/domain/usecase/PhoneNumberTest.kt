package com.nearaid.core.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberTest {

    private val e164 = "+8801712345678"

    @Test
    fun `normalizeBd accepts a leading-zero local number`() {
        assertEquals(e164, PhoneNumber.normalizeBd("01712345678"))
    }

    @Test
    fun `normalizeBd accepts a bare 10-digit national number`() {
        assertEquals(e164, PhoneNumber.normalizeBd("1712345678"))
    }

    @Test
    fun `normalizeBd accepts a number with the 880 country code`() {
        assertEquals(e164, PhoneNumber.normalizeBd("8801712345678"))
    }

    @Test
    fun `normalizeBd strips spaces dashes and a plus sign`() {
        assertEquals(e164, PhoneNumber.normalizeBd("+880 1712-345 678"))
    }

    @Test
    fun `normalizeBd rejects numbers that are too short`() {
        assertNull(PhoneNumber.normalizeBd("012345"))
    }

    @Test
    fun `normalizeBd rejects national numbers not starting with 1`() {
        assertNull(PhoneNumber.normalizeBd("02123456789"))
    }

    @Test
    fun `normalizeBd rejects blank input`() {
        assertNull(PhoneNumber.normalizeBd(""))
        assertNull(PhoneNumber.normalizeBd("   "))
    }

    @Test
    fun `isValidBd mirrors normalizeBd`() {
        assertTrue(PhoneNumber.isValidBd("01712345678"))
        assertFalse(PhoneNumber.isValidBd("12345"))
    }

    @Test
    fun `formatForDisplay groups a valid E164 number`() {
        assertEquals("+880 1712 345 678", PhoneNumber.formatForDisplay(e164))
    }

    @Test
    fun `formatForDisplay returns non-standard input unchanged`() {
        assertEquals("+88012345", PhoneNumber.formatForDisplay("+88012345"))
    }
}
