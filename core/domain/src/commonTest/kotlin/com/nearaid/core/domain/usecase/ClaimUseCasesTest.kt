package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.FakeClaimRepository
import com.nearaid.core.model.ClaimStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClaimUseCasesTest {

    @Test
    fun claimListing_passes_the_listing_id() = runTest {
        val repo = FakeClaimRepository()
        ClaimListingUseCase(repo).invoke("l7")
        assertEquals("l7", repo.lastClaimListingId)
    }

    @Test
    fun withdraw_markDelivered_confirmReceipt_pass_the_claim_id() = runTest {
        val repo = FakeClaimRepository()
        WithdrawClaimUseCase(repo).invoke("c1")
        MarkDeliveredUseCase(repo).invoke("c2")
        ConfirmReceiptUseCase(repo).invoke("c3")
        assertEquals("c1", repo.lastWithdrawId)
        assertEquals("c2", repo.lastMarkDeliveredId)
        assertEquals("c3", repo.lastConfirmReceiptId)
    }

    @Test
    fun rate_trims_the_comment() = runTest {
        val repo = FakeClaimRepository()
        RateClaimUseCase(repo).invoke("c1", 5, "  great help  ")
        assertEquals("c1", repo.lastRateClaimId)
        assertEquals(5, repo.lastRateScore)
        assertEquals("great help", repo.lastRateComment)
    }

    @Test
    fun rate_maps_a_blank_comment_to_null() = runTest {
        val repo = FakeClaimRepository()
        RateClaimUseCase(repo).invoke("c1", 4, "   ")
        assertNull(repo.lastRateComment)
    }

    @Test
    fun rate_keeps_a_null_comment_null() = runTest {
        val repo = FakeClaimRepository()
        RateClaimUseCase(repo).invoke("c1", 4, null)
        assertNull(repo.lastRateComment)
    }

    @Test
    fun getMyClaims_defaults_to_no_status_filter() = runTest {
        val repo = FakeClaimRepository()
        GetMyClaimsUseCase(repo).invoke()
        assertNull(repo.lastMyClaimsStatus)
    }

    @Test
    fun getMyClaims_forwards_the_status_filter() = runTest {
        val repo = FakeClaimRepository()
        GetMyClaimsUseCase(repo).invoke(ClaimStatus.COMPLETED)
        assertEquals(ClaimStatus.COMPLETED, repo.lastMyClaimsStatus)
    }
}
