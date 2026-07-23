package com.nearaid.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class EnumsTest {

    @Test
    fun appLanguage_fromCode_resolves_known_codes() {
        assertEquals(AppLanguage.BN, AppLanguage.fromCode("bn"))
        assertEquals(AppLanguage.EN, AppLanguage.fromCode("en"))
    }

    @Test
    fun appLanguage_fromCode_defaults_to_BN_for_null() {
        assertEquals(AppLanguage.BN, AppLanguage.fromCode(null))
    }

    @Test
    fun appLanguage_fromCode_defaults_to_BN_for_unknown_code() {
        assertEquals(AppLanguage.BN, AppLanguage.fromCode("fr"))
        assertEquals(AppLanguage.BN, AppLanguage.fromCode(""))
    }

    @Test
    fun appLanguage_code_is_the_wire_value() {
        assertEquals("bn", AppLanguage.BN.code)
        assertEquals("en", AppLanguage.EN.code)
    }

    @Test
    fun appLanguage_fromCode_round_trips_every_entry() {
        for (lang in AppLanguage.entries) {
            assertEquals(lang, AppLanguage.fromCode(lang.code))
        }
    }

    @Test
    fun reportTargetType_exposes_the_wire_string() {
        assertEquals("user", ReportTargetType.USER.wire)
        assertEquals("listing", ReportTargetType.LISTING.wire)
    }
}
