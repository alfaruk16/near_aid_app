package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingDetail
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import com.nearaid.core.model.Page

interface ListingRepository {
    suspend fun getNearby(query: DiscoveryQuery, cursor: String? = null): DataResult<Page<ListingCard>>
    suspend fun getListing(id: String): DataResult<ListingDetail>
    suspend fun createListing(input: NewListing): DataResult<ListingDetail>
    suspend fun cancelListing(id: String, reason: String): DataResult<Unit>
    suspend fun getMyListings(type: ListingType, status: ListingStatus? = null): DataResult<List<ListingCard>>
}
