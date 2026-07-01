package com.nearaid.feature.profile.publicprofile

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.GetPublicUserUseCase
import com.nearaid.core.domain.usecase.GetUserRatingsUseCase
import com.nearaid.core.model.Page
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.Rating
import com.nearaid.feature.profile.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PublicProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPublicUser = mockk<GetPublicUserUseCase>()
    private val getUserRatings = mockk<GetUserRatingsUseCase>()

    private fun viewModel() = PublicProfileViewModel(
        getPublicUser = getPublicUser,
        getUserRatings = getUserRatings,
    )

    @Test
    fun `Load populates the user and their ratings`() {
        val user = publicUser("u1")
        val rating = rating("r1")
        coEvery { getPublicUser("u1") } returns DataResult.Success(user)
        coEvery { getUserRatings("u1") } returns DataResult.Success(page(listOf(rating)))

        val viewModel = viewModel()
        viewModel.onIntent(PublicProfileIntent.Load("u1"))
        val state = viewModel.state.value

        assertEquals(user, state.user)
        assertEquals(listOf(rating), state.ratings)
        assertFalse(state.loading)
        assertNull(state.error)
    }

    @Test
    fun `Load with no ratings leaves the list empty`() {
        coEvery { getPublicUser("u1") } returns DataResult.Success(publicUser("u1"))
        coEvery { getUserRatings("u1") } returns DataResult.Success(page(emptyList()))

        val viewModel = viewModel()
        viewModel.onIntent(PublicProfileIntent.Load("u1"))
        val state = viewModel.state.value

        assertTrue(state.ratings.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `Load surfaces an error when the user fetch fails`() {
        coEvery { getPublicUser("u1") } returns DataResult.Failure(AppError.NotFound("no such user"))
        coEvery { getUserRatings("u1") } returns DataResult.Success(page(emptyList()))

        val viewModel = viewModel()
        viewModel.onIntent(PublicProfileIntent.Load("u1"))
        val state = viewModel.state.value

        assertEquals("no such user", state.error)
        assertNull(state.user)
        assertFalse(state.loading)
    }

    @Test
    fun `Load still shows the user when ratings fail`() {
        val user = publicUser("u1")
        coEvery { getPublicUser("u1") } returns DataResult.Success(user)
        coEvery { getUserRatings("u1") } returns DataResult.Failure(AppError.Server(500, "ratings down"))

        val viewModel = viewModel()
        viewModel.onIntent(PublicProfileIntent.Load("u1"))
        val state = viewModel.state.value

        assertEquals(user, state.user)
        assertTrue(state.ratings.isEmpty())
        assertNull(state.error)
        assertFalse(state.loading)
    }

    @Test
    fun `BackClicked emits NavigateBack`() = runTest {
        val viewModel = viewModel()

        viewModel.effect.test {
            viewModel.onIntent(PublicProfileIntent.BackClicked)
            assertEquals(PublicProfileEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun publicUser(id: String) = PublicUser(
        id = id,
        displayName = "Karim",
        photoUrl = null,
        trustScore = 4.2,
        isIdVerified = true,
        aggregateRating = 4.0,
        completedHelpCount = 7,
    )

    private fun rating(id: String) = Rating(
        id = id,
        raterName = "Sultana",
        raterPhotoUrl = null,
        score = 5,
        comment = "Very helpful",
        createdAt = "2026-01-01T00:00:00",
    )

    private fun page(items: List<Rating>) = Page(
        items = items,
        nextCursor = null,
        hasMore = false,
    )
}
