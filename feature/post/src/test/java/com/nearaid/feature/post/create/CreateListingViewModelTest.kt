package com.nearaid.feature.post.create

import app.cash.turbine.test
import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.usecase.CreateListingUseCase
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase
import com.nearaid.core.domain.usecase.RefreshCategoriesUseCase
import com.nearaid.core.model.Author
import com.nearaid.core.model.Category
import com.nearaid.core.model.ListingDetail
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import com.nearaid.core.model.Urgency
import com.nearaid.feature.post.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateListingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCategories = mockk<ObserveCategoriesUseCase>()
    private val refreshCategories = mockk<RefreshCategoriesUseCase>()
    private val createListing = mockk<CreateListingUseCase>()

    @Before
    fun setUp() {
        // Safe defaults so construction (which observes + refreshes categories) never throws.
        every { observeCategories() } returns flowOf(emptyList())
        coEvery { refreshCategories() } returns DataResult.Success(emptyList())
    }

    private fun viewModel() = CreateListingViewModel(
        observeCategories = observeCategories,
        refreshCategories = refreshCategories,
        createListing = createListing,
    )

    // --- category loading ----------------------------------------------------

    @Test
    fun `observing categories populates state and auto-selects the first`() {
        val categories = listOf(category(1), category(2))
        every { observeCategories() } returns flowOf(categories)

        val state = viewModel().state.value

        assertEquals(categories, state.categories)
        assertEquals(1, state.selectedCategoryId)
        coVerify(exactly = 1) { refreshCategories() }
    }

    @Test
    fun `auto-select does not override an explicit selection on re-emission`() {
        val categoriesFlow = MutableStateFlow(listOf(category(1), category(2)))
        every { observeCategories() } returns categoriesFlow
        val viewModel = viewModel()
        assertEquals(1, viewModel.state.value.selectedCategoryId) // auto-selected first

        viewModel.onIntent(CreateListingIntent.CategorySelected(2))
        assertEquals(2, viewModel.state.value.selectedCategoryId)

        categoriesFlow.value = listOf(category(1), category(2), category(3)) // re-emit
        assertEquals(2, viewModel.state.value.selectedCategoryId) // preserved
    }

    @Test
    fun `a failing category refresh is ignored and observed categories still load`() {
        val categories = listOf(category(1))
        every { observeCategories() } returns flowOf(categories)
        coEvery { refreshCategories() } returns DataResult.Failure(AppError.Network("offline"))

        val state = viewModel().state.value

        assertEquals(categories, state.categories)
        assertNull(state.error)
    }

    // --- field-update intents ------------------------------------------------

    @Test
    fun `TitleChanged updates the title and clears any error`() {
        val viewModel = viewModel()
        // Put an error on the state first (blank-title submit with a category selected).
        viewModel.onIntent(CreateListingIntent.CategorySelected(1))
        viewModel.onIntent(CreateListingIntent.Submit(ListingType.REQUEST))
        assertEquals("Please enter a title", viewModel.state.value.error)

        viewModel.onIntent(CreateListingIntent.TitleChanged("Rice"))

        assertEquals("Rice", viewModel.state.value.title)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `DescriptionChanged updates the description`() {
        val viewModel = viewModel()

        viewModel.onIntent(CreateListingIntent.DescriptionChanged("Two bags"))

        assertEquals("Two bags", viewModel.state.value.description)
    }

    @Test
    fun `QuantityChanged updates the quantity`() {
        val viewModel = viewModel()

        viewModel.onIntent(CreateListingIntent.QuantityChanged("5 kg"))

        assertEquals("5 kg", viewModel.state.value.quantity)
    }

    @Test
    fun `UrgencySelected updates the urgency`() {
        val viewModel = viewModel()

        viewModel.onIntent(CreateListingIntent.UrgencySelected(Urgency.CRITICAL))

        assertEquals(Urgency.CRITICAL, viewModel.state.value.urgency)
    }

    @Test
    fun `AvailableUntilChanged updates the availability window`() {
        val viewModel = viewModel()

        viewModel.onIntent(CreateListingIntent.AvailableUntilChanged("8:00 PM"))

        assertEquals("8:00 PM", viewModel.state.value.availableUntil)
    }

    @Test
    fun `CategorySelected updates the selection and clears any error`() {
        every { observeCategories() } returns flowOf(listOf(category(1), category(2)))
        val viewModel = viewModel()
        viewModel.onIntent(CreateListingIntent.Submit(ListingType.REQUEST)) // blank title -> error
        assertEquals("Please enter a title", viewModel.state.value.error)

        viewModel.onIntent(CreateListingIntent.CategorySelected(2))

        assertEquals(2, viewModel.state.value.selectedCategoryId)
        assertNull(viewModel.state.value.error)
    }

    // --- canSubmit gating (state computed property) --------------------------

    @Test
    fun `canSubmit requires a category a title and no in-flight submit`() {
        assertFalse(CreateListingState().canSubmit)
        assertFalse(CreateListingState(selectedCategoryId = 1).canSubmit) // blank title
        assertFalse(CreateListingState(title = "Rice").canSubmit) // no category
        assertFalse(CreateListingState(selectedCategoryId = 1, title = "  ").canSubmit) // blank title
        assertFalse(CreateListingState(selectedCategoryId = 1, title = "Rice", loading = true).canSubmit)
        assertTrue(CreateListingState(selectedCategoryId = 1, title = "Rice").canSubmit)
    }

    // --- submit --------------------------------------------------------------

    @Test
    fun `submit does nothing when no category is selected`() {
        every { observeCategories() } returns flowOf(emptyList()) // stays null, no auto-select
        val viewModel = viewModel()
        viewModel.onIntent(CreateListingIntent.TitleChanged("Rice"))

        viewModel.onIntent(CreateListingIntent.Submit(ListingType.REQUEST))

        coVerify(exactly = 0) { createListing(any()) }
        assertNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `submit with a blank title sets an error and does not call the use case`() {
        every { observeCategories() } returns flowOf(listOf(category(1)))
        val viewModel = viewModel()

        viewModel.onIntent(CreateListingIntent.Submit(ListingType.REQUEST))

        assertEquals("Please enter a title", viewModel.state.value.error)
        coVerify(exactly = 0) { createListing(any()) }
    }

    @Test
    fun `submitting a REQUEST maps urgency and posts, emitting the created id`() = runTest {
        every { observeCategories() } returns flowOf(listOf(category(7)))
        val slot = slot<NewListing>()
        coEvery { createListing(capture(slot)) } returns DataResult.Success(listingDetail("listing-42"))
        val viewModel = viewModel()
        viewModel.onIntent(CreateListingIntent.TitleChanged("Rice"))
        viewModel.onIntent(CreateListingIntent.DescriptionChanged("Two bags"))
        viewModel.onIntent(CreateListingIntent.QuantityChanged("5 kg"))
        viewModel.onIntent(CreateListingIntent.UrgencySelected(Urgency.HIGH))

        viewModel.effect.test {
            viewModel.onIntent(CreateListingIntent.Submit(ListingType.REQUEST))
            assertEquals(CreateListingEffect.Posted("listing-42"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val input = slot.captured
        assertEquals(ListingType.REQUEST, input.type)
        assertEquals(7, input.categoryId)
        assertEquals("Rice", input.title)
        assertEquals("Two bags", input.description)
        assertEquals("5 kg", input.quantity)
        assertEquals(Urgency.HIGH, input.urgency)
        assertNull(input.availableUntil) // requests carry no availability window
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `submitting an OFFER maps the availability window and nulls urgency`() {
        every { observeCategories() } returns flowOf(listOf(category(3)))
        val slot = slot<NewListing>()
        coEvery { createListing(capture(slot)) } returns DataResult.Success(listingDetail("l1"))
        val viewModel = viewModel()
        viewModel.onIntent(CreateListingIntent.TitleChanged("Blankets"))
        viewModel.onIntent(CreateListingIntent.UrgencySelected(Urgency.HIGH))
        viewModel.onIntent(CreateListingIntent.AvailableUntilChanged("8:00 PM"))

        viewModel.onIntent(CreateListingIntent.Submit(ListingType.OFFER))

        val input = slot.captured
        assertEquals(ListingType.OFFER, input.type)
        assertNull(input.urgency) // offers carry no urgency
        assertEquals("8:00 PM", input.availableUntil)
    }

    @Test
    fun `submit trims text fields and treats a blank quantity as null`() {
        every { observeCategories() } returns flowOf(listOf(category(1)))
        val slot = slot<NewListing>()
        coEvery { createListing(capture(slot)) } returns DataResult.Success(listingDetail("l1"))
        val viewModel = viewModel()
        viewModel.onIntent(CreateListingIntent.TitleChanged("  Rice  "))
        viewModel.onIntent(CreateListingIntent.DescriptionChanged("  desc  "))
        viewModel.onIntent(CreateListingIntent.QuantityChanged("   "))

        viewModel.onIntent(CreateListingIntent.Submit(ListingType.REQUEST))

        val input = slot.captured
        assertEquals("Rice", input.title)
        assertEquals("desc", input.description)
        assertNull(input.quantity)
    }

    @Test
    fun `submit failure surfaces the error and clears loading`() {
        every { observeCategories() } returns flowOf(listOf(category(1)))
        coEvery { createListing(any()) } returns DataResult.Failure(AppError.Server(500, "server down"))
        val viewModel = viewModel()
        viewModel.onIntent(CreateListingIntent.TitleChanged("Rice"))

        viewModel.onIntent(CreateListingIntent.Submit(ListingType.REQUEST))

        assertEquals("server down", viewModel.state.value.error)
        assertFalse(viewModel.state.value.loading)
    }

    // --- fixtures ------------------------------------------------------------

    private fun category(id: Int) = Category(
        id = id,
        key = "key-$id",
        nameEn = "Category $id",
        nameBn = "বিভাগ $id",
        icon = null,
    )

    private fun listingDetail(id: String) = ListingDetail(
        id = id,
        type = ListingType.REQUEST,
        status = ListingStatus.OPEN,
        title = "title",
        description = "description",
        quantity = null,
        category = null,
        urgency = null,
        availableUntil = null,
        areaLabel = null,
        locationFuzzed = null,
        locationExact = null,
        images = emptyList(),
        author = Author(
            id = "author",
            displayName = null,
            photoUrl = null,
            trustScore = null,
            isIdVerified = false,
        ),
        expiresAt = null,
        createdAt = "2026-01-01T00:00:00",
    )
}
