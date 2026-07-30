package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.database.dao.ListingCacheDao
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import com.nearaid.core.model.Urgency
import com.nearaid.core.network.api.ListingApi
import com.nearaid.core.network.dto.AuthorDto
import com.nearaid.core.network.dto.CreateListingBody
import com.nearaid.core.network.dto.ListingCardDto
import com.nearaid.core.network.dto.ListingDetailDto
import com.nearaid.core.network.dto.MyListingsResponse
import com.nearaid.core.network.dto.NearbyResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ListingRepositoryImplTest {

    private val api = mockk<ListingApi>()
    private val cache = mockk<ListingCacheDao>(relaxUnitFun = true)
    private fun repo() = ListingRepositoryImpl(api, cache, UnconfinedTestDispatcher())

    private val query = DiscoveryQuery(type = ListingType.OFFER, lat = 1.0, lng = 2.0, urgency = Urgency.HIGH)
    private fun card(id: String) = ListingCardDto(id = id, type = "offer", title = "Rice", author = AuthorDto("u1"))

    @Test
    fun getNearby_success_maps_page_and_caches_first_page() = runTest {
        coEvery {
            api.getNearby("offer", 1.0, 2.0, 5.0, null, "high", null, null)
        } returns NearbyResponse(results = listOf(card("l1")), nextCursor = "n2", hasMore = true)

        val result = repo().getNearby(query, cursor = null)

        assertTrue(result is DataResult.Success)
        val page = (result as DataResult.Success).data
        assertEquals("l1", page.items.single().id)
        assertEquals("n2", page.nextCursor)
        assertTrue(page.hasMore)
        // First page refreshes the cache.
        coVerify(exactly = 1) { cache.clearByType("offer") }
        coVerify(exactly = 1) { cache.upsertAll(any()) }
    }

    @Test
    fun getNearby_failure_on_first_page_with_empty_cache_returns_failure() = runTest {
        coEvery { api.getNearby(any(), any(), any(), any(), any(), any(), any(), null) } throws IOException("offline")
        coEvery { cache.getByType("offer") } returns emptyList()

        val result = repo().getNearby(query, cursor = null)

        assertTrue(result is DataResult.Failure)
    }

    @Test
    fun getListing_maps_detail() = runTest {
        coEvery { api.getListing("l1") } returns ListingDetailDto(id = "l1", type = "request", title = "Help", author = AuthorDto("u1"))
        val result = repo().getListing("l1")
        assertTrue(result is DataResult.Success)
        assertEquals(ListingType.REQUEST, (result as DataResult.Success).data.type)
    }

    @Test
    fun createListing_builds_body_from_input() = runTest {
        val slot = mutableListOf<CreateListingBody>()
        coEvery { api.createListing(capture(slot)) } returns
            ListingDetailDto(id = "l1", type = "offer", title = "Rice", author = AuthorDto("u1"))

        val input = NewListing(
            type = ListingType.OFFER,
            categoryId = 3,
            title = "Rice",
            description = "5kg",
            quantity = null,
            urgency = null,
            availableUntil = null,
            lat = 1.0,
            lng = 2.0,
            areaLabel = null,
        )
        repo().createListing(input)

        val body = slot.single()
        assertEquals("offer", body.type)
        assertEquals(3, body.categoryId)
        assertEquals("", body.quantity) // null coerced to ""
        assertEquals("", body.areaLabel)
    }

    @Test
    fun cancelAndMyListings_delegate_with_wire_values() = runTest {
        coEvery { api.cancelListing(any(), any()) } returns Unit
        coEvery { api.getMyListings("request", "delivered") } returns MyListingsResponse(results = listOf(card("l1")))

        assertTrue(repo().cancelListing("l1", "expired") is DataResult.Success)
        val my = repo().getMyListings(ListingType.REQUEST, ListingStatus.DELIVERED)

        assertTrue(my is DataResult.Success)
        coVerify(exactly = 1) { api.getMyListings("request", "delivered") }
    }
}
