package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus

interface ClaimRepository {
    suspend fun claim(listingId: String): DataResult<Claim>
    suspend fun withdraw(claimId: String): DataResult<Unit>
    suspend fun markDelivered(claimId: String): DataResult<Unit>
    suspend fun confirmReceipt(claimId: String): DataResult<Unit>
    suspend fun rate(claimId: String, score: Int, comment: String?): DataResult<Unit>
    suspend fun getMyClaims(status: ClaimStatus? = null): DataResult<List<Claim>>
}
