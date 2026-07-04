package com.nearaid.core.common.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DataResultTest {

    private val success: DataResult<Int> = DataResult.Success(2)
    private val failure: DataResult<Int> = DataResult.Failure(AppError.NotFound("missing"))

    @Test
    fun map_transforms_the_success_value() {
        val mapped = success.map { it * 10 }
        assertEquals(DataResult.Success(20), mapped)
    }

    @Test
    fun map_leaves_a_failure_untouched() {
        val mapped = failure.map { it * 10 }
        assertSame(failure, mapped)
    }

    @Test
    fun onSuccess_runs_only_for_success() {
        var seen: Int? = null
        success.onSuccess { seen = it }
        assertEquals(2, seen)

        seen = null
        failure.onSuccess { seen = it }
        assertNull(seen)
    }

    @Test
    fun onSuccess_returns_the_same_instance_for_chaining() {
        assertSame(success, success.onSuccess { })
    }

    @Test
    fun onFailure_runs_only_for_failure() {
        var error: AppError? = null
        failure.onFailure { error = it }
        assertEquals(AppError.NotFound("missing"), error)

        error = null
        success.onFailure { error = it }
        assertNull(error)
    }

    @Test
    fun getOrNull_returns_data_for_success_and_null_for_failure() {
        assertEquals(2, success.getOrNull())
        assertNull(failure.getOrNull())
    }

    @Test
    fun success_and_failure_are_distinguishable() {
        assertTrue(success is DataResult.Success)
        assertFalse(failure is DataResult.Success)
    }
}
