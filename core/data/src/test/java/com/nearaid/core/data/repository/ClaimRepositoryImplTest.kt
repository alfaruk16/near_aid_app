package com.nearaid.core.data.repository

import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.api.ListingApi
import com.nearaid.core.network.dto.ClaimDto
import com.nearaid.core.network.dto.MyClaimDto
import com.nearaid.core.network.dto.MyClaimListingDto
import com.nearaid.core.network.dto.MyClaimsResponse
import com.nearaid.core.network.dto.RatingBody
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
class ClaimRepositoryImplTest {

    private val listingApi = mockk<ListingApi>()
    private val claimApi = mockk<ClaimApi>(relaxUnitFun = true)

    private fun repo() = ClaimRepositoryImpl(listingApi, claimApi, UnconfinedTestDispatcher())

    @Test
    fun claim_maps_the_returned_dto_to_domain() = runTest {
        coEvery { listingApi.claim("l1") } returns ClaimDto(claimId = "c1", listingId = "l1", status = "active")

        val result = repo().claim("l1")

        assertTrue(result is DataResult.Success)
        val claim = (result as DataResult.Success).data
        assertEquals("c1", claim.id)
        assertEquals("l1", claim.listingId)
    }

    @Test
    fun getMyClaims_passes_wire_status_and_maps_nested_shape() = runTest {
        coEvery { claimApi.getMyClaims("active") } returns MyClaimsResponse(
            results = listOf(
                MyClaimDto(
                    id = "c1",
                    status = "active",
                    deliveredAt = "2026-07-29T00:00:00Z",
                    listing = MyClaimListingDto(id = "l1", type = "offer"),
                ),
            ),
        )

        val result = repo().getMyClaims(ClaimStatus.ACTIVE)

        assertTrue(result is DataResult.Success)
        val claim = (result as DataResult.Success).data.single()
        assertEquals("l1", claim.listingId)
        assertEquals(ListingType.OFFER, claim.listingType)
        assertEquals(ClaimStatus.DELIVERED, claim.status) // derived from delivered_at
        coVerify(exactly = 1) { claimApi.getMyClaims("active") }
    }

    @Test
    fun getMyClaims_forwards_null_status() = runTest {
        coEvery { claimApi.getMyClaims(null) } returns MyClaimsResponse()

        val result = repo().getMyClaims(null)

        assertTrue(result is DataResult.Success)
        coVerify(exactly = 1) { claimApi.getMyClaims(null) }
    }

    @Test
    fun handoff_actions_delegate_to_the_right_endpoints() = runTest {
        val repo = repo()

        assertTrue(repo.markDelivered("c1") is DataResult.Success)
        assertTrue(repo.confirmReceipt("c2") is DataResult.Success)
        assertTrue(repo.withdraw("c3") is DataResult.Success)

        coVerify(exactly = 1) { claimApi.deliver("c1") }
        coVerify(exactly = 1) { claimApi.confirm("c2") }
        coVerify(exactly = 1) { claimApi.withdraw("c3") }
    }

    @Test
    fun rate_forwards_score_and_comment_body() = runTest {
        repo().rate("c1", score = 5, comment = "thanks")
        coVerify(exactly = 1) { claimApi.rate("c1", RatingBody(5, "thanks")) }
    }

    @Test
    fun transport_failure_is_mapped_to_a_network_error() = runTest {
        coEvery { claimApi.getMyClaims(any()) } throws IOException("offline")

        val result = repo().getMyClaims(null)

        assertTrue(result is DataResult.Failure)
        assertTrue((result as DataResult.Failure).error is AppError.Network)
    }
}
