package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.ListingRepository
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingDetail
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import com.nearaid.core.model.Page
import javax.inject.Inject

class GetNearbyListingsUseCase @Inject constructor(
    private val repository: ListingRepository,
) {
    suspend operator fun invoke(query: DiscoveryQuery, cursor: String? = null): DataResult<Page<ListingCard>> =
        repository.getNearby(query, cursor)
}

class GetListingUseCase @Inject constructor(
    private val repository: ListingRepository,
) {
    suspend operator fun invoke(id: String): DataResult<ListingDetail> = repository.getListing(id)
}

class CreateListingUseCase @Inject constructor(
    private val repository: ListingRepository,
) {
    suspend operator fun invoke(input: NewListing): DataResult<ListingDetail> =
        repository.createListing(input)
}

class CancelListingUseCase @Inject constructor(
    private val repository: ListingRepository,
) {
    suspend operator fun invoke(id: String, reason: String): DataResult<Unit> =
        repository.cancelListing(id, reason)
}

class GetMyListingsUseCase @Inject constructor(
    private val repository: ListingRepository,
) {
    suspend operator fun invoke(type: ListingType, status: ListingStatus? = null): DataResult<List<ListingCard>> =
        repository.getMyListings(type, status)
}
