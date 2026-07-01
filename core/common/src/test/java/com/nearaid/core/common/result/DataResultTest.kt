package com.nearaid.core.common.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DataResultTest {

    private val success: DataResult<Int> = DataResult.Success(2)
    private val failure: DataResult<Int> = DataResult.Failure(AppError.NotFound("missing"))

    @Test
    fun `map transforms the success value`() {
        val mapped = success.map { it * 10 }
        assertEquals(DataResult.Success(20), mapped)
    }

    @Test
    fun `map leaves a failure untouched`() {
        val mapped = failure.map { it * 10 }
        assertSame(failure, mapped)
    }

    @Test
    fun `onSuccess runs only for success`() {
        var seen: Int? = null
        success.onSuccess { seen = it }
        assertEquals(2, seen)

        seen = null
        failure.onSuccess { seen = it }
        assertNull(seen)
    }

    @Test
    fun `onSuccess returns the same instance for chaining`() {
        assertSame(success, success.onSuccess { })
    }

    @Test
    fun `onFailure runs only for failure`() {
        var error: AppError? = null
        failure.onFailure { error = it }
        assertEquals(AppError.NotFound("missing"), error)

        error = null
        success.onFailure { error = it }
        assertNull(error)
    }

    @Test
    fun `getOrNull returns data for success and null for failure`() {
        assertEquals(2, success.getOrNull())
        assertNull(failure.getOrNull())
    }

    @Test
    fun `success and failure are distinguishable`() {
        assertTrue(success is DataResult.Success)
        assertFalse(failure is DataResult.Success)
    }
}
