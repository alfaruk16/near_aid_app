package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.FakeListingRepository
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ListingUseCasesTest {

    private val query = DiscoveryQuery(type = ListingType.REQUEST, lat = 23.7, lng = 90.4)

    @Test
    fun getNearby_forwards_query_and_defaults_cursor_to_null() = runTest {
        val repo = FakeListingRepository()
        GetNearbyListingsUseCase(repo).invoke(query)
        assertSame(query, repo.lastNearbyQuery)
        assertNull(repo.lastNearbyCursor)
    }

    @Test
    fun getNearby_forwards_cursor() = runTest {
        val repo = FakeListingRepository()
        GetNearbyListingsUseCase(repo).invoke(query, "next-1")
        assertEquals("next-1", repo.lastNearbyCursor)
    }

    @Test
    fun getListing_forwards_id() = runTest {
        val repo = FakeListingRepository()
        GetListingUseCase(repo).invoke("l1")
        assertEquals("l1", repo.lastGetListingId)
    }

    @Test
    fun createListing_forwards_input() = runTest {
        val repo = FakeListingRepository()
        val input = NewListing(
            type = ListingType.OFFER,
            categoryId = 3,
            title = "Rice",
            description = "5kg",
            quantity = "5kg",
            urgency = null,
            availableUntil = null,
            lat = 23.7,
            lng = 90.4,
            areaLabel = "Dhanmondi",
        )
        CreateListingUseCase(repo).invoke(input)
        assertSame(input, repo.lastCreateInput)
    }

    @Test
    fun cancelListing_forwards_id_and_reason() = runTest {
        val repo = FakeListingRepository()
        CancelListingUseCase(repo).invoke("l1", "found elsewhere")
        assertEquals("l1", repo.lastCancelId)
        assertEquals("found elsewhere", repo.lastCancelReason)
    }

    @Test
    fun getMyListings_forwards_type_and_defaults_status_to_null() = runTest {
        val repo = FakeListingRepository()
        GetMyListingsUseCase(repo).invoke(ListingType.OFFER)
        assertEquals(ListingType.OFFER, repo.lastMyListingsType)
        assertNull(repo.lastMyListingsStatus)
    }

    @Test
    fun getMyListings_forwards_status_filter() = runTest {
        val repo = FakeListingRepository()
        GetMyListingsUseCase(repo).invoke(ListingType.REQUEST, ListingStatus.CLAIMED)
        assertEquals(ListingType.REQUEST, repo.lastMyListingsType)
        assertEquals(ListingStatus.CLAIMED, repo.lastMyListingsStatus)
    }
}
