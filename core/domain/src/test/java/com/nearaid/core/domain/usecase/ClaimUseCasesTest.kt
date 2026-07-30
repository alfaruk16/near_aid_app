package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.domain.repository.ClaimRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ClaimUseCasesTest {

    private val repository = mockk<ClaimRepository>(relaxed = true)

    private fun claim(id: String) = Claim(
        id = id,
        listingId = "l1",
        listingType = ListingType.REQUEST,
        status = ClaimStatus.ACTIVE,
        chatThreadId = null,
        claimedAt = null,
    )

    @Test
    fun claimListing_delegates_and_returns_repository_result() = runTest {
        val expected = DataResult.Success(claim("c1"))
        coEvery { repository.claim("l1") } returns expected

        val result = ClaimListingUseCase(repository)("l1")

        assertSame(expected, result)
        coVerify(exactly = 1) { repository.claim("l1") }
    }

    @Test
    fun withdraw_markDelivered_confirmReceipt_delegate() = runTest {
        coEvery { repository.withdraw(any()) } returns DataResult.Success(Unit)
        coEvery { repository.markDelivered(any()) } returns DataResult.Success(Unit)
        coEvery { repository.confirmReceipt(any()) } returns DataResult.Success(Unit)

        WithdrawClaimUseCase(repository)("c1")
        MarkDeliveredUseCase(repository)("c2")
        ConfirmReceiptUseCase(repository)("c3")

        coVerify(exactly = 1) { repository.withdraw("c1") }
        coVerify(exactly = 1) { repository.markDelivered("c2") }
        coVerify(exactly = 1) { repository.confirmReceipt("c3") }
    }

    @Test
    fun getMyClaims_forwards_status_and_default() = runTest {
        coEvery { repository.getMyClaims(any()) } returns DataResult.Success(emptyList())

        GetMyClaimsUseCase(repository)(ClaimStatus.COMPLETED)
        GetMyClaimsUseCase(repository)() // default null

        coVerify(exactly = 1) { repository.getMyClaims(ClaimStatus.COMPLETED) }
        coVerify(exactly = 1) { repository.getMyClaims(null) }
    }

    @Test
    fun rateClaim_trims_comment_and_blanks_become_null() = runTest {
        coEvery { repository.rate(any(), any(), any()) } returns DataResult.Success(Unit)
        val useCase = RateClaimUseCase(repository)

        useCase("c1", 5, "  great help  ")
        useCase("c2", 4, "   ")
        useCase("c3", 3, null)

        coVerify(exactly = 1) { repository.rate("c1", 5, "great help") }
        coVerify(exactly = 1) { repository.rate("c2", 4, null) }
        coVerify(exactly = 1) { repository.rate("c3", 3, null) }
    }
}
