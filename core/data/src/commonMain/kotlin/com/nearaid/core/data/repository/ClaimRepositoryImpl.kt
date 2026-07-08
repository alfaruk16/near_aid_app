package com.nearaid.core.data.repository

import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.api.ListingApi
import com.nearaid.core.network.dto.RatingBody
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.data.mapper.wire
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.domain.repository.ClaimRepository
import com.nearaid.core.common.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ClaimRepositoryImpl(
    private val listingApi: ListingApi,
    private val claimApi: ClaimApi,
    private val ioDispatcher: CoroutineDispatcher,
) : ClaimRepository {

    override suspend fun claim(listingId: String): DataResult<Claim> =
        withContext(ioDispatcher) { safeApiCall { listingApi.claim(listingId).toDomain() } }

    override suspend fun withdraw(claimId: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { claimApi.withdraw(claimId) } }

    override suspend fun markDelivered(claimId: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { claimApi.deliver(claimId) } }

    override suspend fun confirmReceipt(claimId: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { claimApi.confirm(claimId) } }

    override suspend fun rate(claimId: String, score: Int, comment: String?): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { claimApi.rate(claimId, RatingBody(score, comment)) } }

    override suspend fun getMyClaims(status: ClaimStatus?): DataResult<List<Claim>> =
        withContext(ioDispatcher) {
            safeApiCall { claimApi.getMyClaims(status?.wire()).results.map { it.toDomain() } }
        }
}
