package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.domain.repository.ListingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ListingUseCasesTest {

    private val repository = mockk<ListingRepository>(relaxed = true)

    private val query = DiscoveryQuery(type = ListingType.REQUEST, lat = 1.0, lng = 2.0)

    @Test
    fun getNearby_forwards_query_with_default_null_cursor() = runTest {
        GetNearbyListingsUseCase(repository)(query)
        coVerify(exactly = 1) { repository.getNearby(query, null) }
    }

    @Test
    fun getNearby_forwards_explicit_cursor() = runTest {
        GetNearbyListingsUseCase(repository)(query, "next")
        coVerify(exactly = 1) { repository.getNearby(query, "next") }
    }

    @Test
    fun getListing_and_cancelListing_delegate() = runTest {
        coEvery { repository.cancelListing(any(), any()) } returns DataResult.Success(Unit)

        GetListingUseCase(repository)("l1")
        CancelListingUseCase(repository)("l1", "expired")

        coVerify(exactly = 1) { repository.getListing("l1") }
        coVerify(exactly = 1) { repository.cancelListing("l1", "expired") }
    }

    @Test
    fun getMyListings_forwards_type_and_status_default() = runTest {
        GetMyListingsUseCase(repository)(ListingType.OFFER)
        GetMyListingsUseCase(repository)(ListingType.REQUEST, ListingStatus.DELIVERED)

        coVerify(exactly = 1) { repository.getMyListings(ListingType.OFFER, null) }
        coVerify(exactly = 1) { repository.getMyListings(ListingType.REQUEST, ListingStatus.DELIVERED) }
    }
}
