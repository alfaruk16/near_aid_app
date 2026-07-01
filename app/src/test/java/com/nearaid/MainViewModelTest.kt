package com.nearaid

import com.nearaid.core.domain.usecase.ObserveLoginStateUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeLoginState = mockk<ObserveLoginStateUseCase>()

    private fun viewModel() = MainViewModel(observeLoginState)

    @Test
    fun `isLoggedIn stays null while nobody is collecting`() {
        // WhileSubscribed keeps the initial value (splash up) until there is a subscriber.
        every { observeLoginState() } returns flowOf(true)

        assertNull(viewModel().isLoggedIn.value)
    }

    @Test
    fun `isLoggedIn resolves to true when the user is logged in`() = runTest {
        every { observeLoginState() } returns flowOf(true)
        val viewModel = viewModel()

        // A background collector activates the WhileSubscribed upstream.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.isLoggedIn.collect { }
        }

        assertEquals(true, viewModel.isLoggedIn.value)
    }

    @Test
    fun `isLoggedIn resolves to false when the user is logged out`() = runTest {
        every { observeLoginState() } returns flowOf(false)
        val viewModel = viewModel()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.isLoggedIn.collect { }
        }

        assertEquals(false, viewModel.isLoggedIn.value)
    }

    @Test
    fun `isLoggedIn tracks subsequent login-state changes`() = runTest {
        val loginState = MutableStateFlow(false)
        every { observeLoginState() } returns loginState
        val viewModel = viewModel()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.isLoggedIn.collect { }
        }
        assertEquals(false, viewModel.isLoggedIn.value)

        loginState.value = true

        assertEquals(true, viewModel.isLoggedIn.value)
    }
}
