package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.ClaimRepository
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus

class ClaimListingUseCase(
    private val repository: ClaimRepository,
) {
    suspend operator fun invoke(listingId: String): DataResult<Claim> = repository.claim(listingId)
}

class WithdrawClaimUseCase(
    private val repository: ClaimRepository,
) {
    suspend operator fun invoke(claimId: String): DataResult<Unit> = repository.withdraw(claimId)
}

class MarkDeliveredUseCase(
    private val repository: ClaimRepository,
) {
    suspend operator fun invoke(claimId: String): DataResult<Unit> = repository.markDelivered(claimId)
}

class ConfirmReceiptUseCase(
    private val repository: ClaimRepository,
) {
    suspend operator fun invoke(claimId: String): DataResult<Unit> = repository.confirmReceipt(claimId)
}

class RateClaimUseCase(
    private val repository: ClaimRepository,
) {
    suspend operator fun invoke(claimId: String, score: Int, comment: String?): DataResult<Unit> =
        repository.rate(claimId, score, comment?.trim()?.ifBlank { null })
}

class GetMyClaimsUseCase(
    private val repository: ClaimRepository,
) {
    suspend operator fun invoke(status: ClaimStatus? = null): DataResult<List<Claim>> =
        repository.getMyClaims(status)
}
