package com.nearaid.core.common.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppErrorTest {

    @Test
    fun each_variant_exposes_its_message_through_the_base_type() {
        val errors: List<AppError> = listOf(
            AppError.Network("net"),
            AppError.Unauthorized("401"),
            AppError.Forbidden("403"),
            AppError.NotFound("404"),
            AppError.Conflict("409"),
            AppError.RateLimited("429"),
            AppError.Unknown("?"),
        )
        assertEquals(
            listOf("net", "401", "403", "404", "409", "429", "?"),
            errors.map { it.message },
        )
    }

    @Test
    fun validation_carries_field_errors() {
        val error = AppError.Validation(message = "bad", fieldErrors = mapOf("phone" to listOf("required")))
        assertEquals("bad", error.message)
        assertEquals(listOf("required"), error.fieldErrors["phone"])
    }

    @Test
    fun validation_defaults_to_empty_field_errors() {
        assertEquals(emptyMap(), AppError.Validation().fieldErrors)
    }

    @Test
    fun server_carries_the_status_code() {
        val error = AppError.Server(code = 503, message = "down")
        assertEquals(503, error.code)
        assertEquals("down", error.message)
    }

    @Test
    fun messages_default_to_null() {
        assertNull(AppError.Network().message)
        assertNull(AppError.Unknown().message)
    }
}
