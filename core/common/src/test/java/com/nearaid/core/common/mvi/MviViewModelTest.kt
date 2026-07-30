package com.nearaid.core.common.mvi

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MviViewModelTest {

    // A minimal concrete MVI ViewModel exercising the base machinery.
    private data class CounterState(val count: Int = 0) : UiState
    private sealed interface CounterIntent : UiIntent {
        data object Increment : CounterIntent
        data object Emit : CounterIntent
    }
    private data class Toast(val text: String) : UiEffect

    private class CounterViewModel : MviViewModel<CounterState, CounterIntent, Toast>() {
        override fun initialState() = CounterState()
        override fun onIntent(intent: CounterIntent) = when (intent) {
            CounterIntent.Increment -> setState { copy(count = count + 1) }
            CounterIntent.Emit -> sendEffect(Toast("hi"))
        }
    }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun starts_in_initial_state() {
        assertEquals(CounterState(0), CounterViewModel().state.value)
    }

    @Test
    fun onIntent_reduces_state_via_setState() {
        val vm = CounterViewModel()
        vm.onIntent(CounterIntent.Increment)
        vm.onIntent(CounterIntent.Increment)
        assertEquals(2, vm.state.value.count)
    }

    @Test
    fun sendEffect_emits_one_off_effects() = runTest {
        val vm = CounterViewModel()
        vm.effect.test {
            vm.onIntent(CounterIntent.Emit)
            assertEquals(Toast("hi"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
