package com.nearaid.core.common.mvi

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

private data class CounterState(val count: Int = 0) : UiState

private sealed interface CounterIntent : UiIntent {
    data object Increment : CounterIntent
    data class Add(val n: Int) : CounterIntent
    data object Finish : CounterIntent
}

private sealed interface CounterEffect : UiEffect {
    data class Done(val total: Int) : CounterEffect
}

private class CounterViewModel : MviViewModel<CounterState, CounterIntent, CounterEffect>() {
    override fun initialState() = CounterState()

    override fun onIntent(intent: CounterIntent) {
        when (intent) {
            CounterIntent.Increment -> setState { copy(count = count + 1) }
            is CounterIntent.Add -> setState { copy(count = count + intent.n) }
            CounterIntent.Finish -> sendEffect(CounterEffect.Done(currentState.count))
        }
    }
}

class MviViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun starts_in_the_initial_state() = runTest(dispatcher) {
        assertEquals(CounterState(0), CounterViewModel().state.first())
    }

    @Test
    fun setState_reduces_state_from_intents() = runTest(dispatcher) {
        val vm = CounterViewModel()
        vm.onIntent(CounterIntent.Increment)
        vm.onIntent(CounterIntent.Add(5))
        assertEquals(CounterState(6), vm.state.value)
    }

    @Test
    fun sendEffect_emits_a_one_off_effect_reflecting_current_state() = runTest(dispatcher) {
        val vm = CounterViewModel()
        vm.onIntent(CounterIntent.Add(3))
        vm.effect.test {
            vm.onIntent(CounterIntent.Finish)
            assertEquals(CounterEffect.Done(3), awaitItem())
        }
    }
}
