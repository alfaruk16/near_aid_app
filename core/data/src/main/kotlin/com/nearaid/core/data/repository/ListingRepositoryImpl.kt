package com.nearaid.core.data.repository

import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.database.dao.ListingCacheDao
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.data.mapper.toEntity
import com.nearaid.core.network.api.ListingApi
import com.nearaid.core.network.dto.CancelBody
import com.nearaid.core.network.dto.CreateListingBody
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.data.mapper.wire
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingDetail
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import com.nearaid.core.model.Page
import com.nearaid.core.domain.repository.ListingRepository
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.common.dispatcher.Dispatcher
import com.nearaid.core.common.dispatcher.NearAidDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListingRepositoryImpl @Inject constructor(
    private val listingApi: ListingApi,
    private val cacheDao: ListingCacheDao,
    @Dispatcher(NearAidDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : ListingRepository {

    override suspend fun getNearby(query: DiscoveryQuery, cursor: String?): DataResult<Page<ListingCard>> =
        withContext(ioDispatcher) {
            val typeWire = query.type.wire()
            val result = safeApiCall {
                listingApi.getNearby(
                    type = typeWire,
                    lat = query.lat,
                    lng = query.lng,
                    radiusKm = query.radiusKm,
                    categories = query.categories.ifEmpty { null },
                    urgency = query.urgency?.wire(),
                    query = query.query?.takeIf { it.isNotBlank() },
                    cursor = cursor,
                )
            }
            when (result) {
                is DataResult.Success -> {
                    val page = Page(
                        items = result.data.results.map { it.toDomain() },
                        nextCursor = result.data.nextCursor,
                        hasMore = result.data.hasMore,
                    )
                    if (cursor == null) {
                        cacheDao.clearByType(typeWire)
                        cacheDao.upsertAll(page.items.map { it.toEntity(typeWire) })
                    }
                    DataResult.Success(page)
                }
                is DataResult.Failure -> {
                    if (cursor == null) {
                        val cached = cacheDao.getByType(typeWire).map { it.toDomain() }
                        if (cached.isNotEmpty()) DataResult.Success(Page(cached, null, false)) else result
                    } else {
                        result
                    }
                }
            }
        }

    override suspend fun getListing(id: String): DataResult<ListingDetail> =
        withContext(ioDispatcher) { safeApiCall { listingApi.getListing(id).toDomain() } }

    override suspend fun createListing(input: NewListing): DataResult<ListingDetail> =
        withContext(ioDispatcher) {
            val body = CreateListingBody(
                type = input.type.wire(),
                categoryId = input.categoryId,
                title = input.title,
                description = input.description,
                quantity = input.quantity.orEmpty(),
                urgency = input.urgency?.wire(),
                availableUntil = input.availableUntil,
                lat = input.lat,
                lng = input.lng,
                areaLabel = input.areaLabel.orEmpty(),
                imageUrls = input.imageUrls,
            )
            safeApiCall { listingApi.createListing(body).toDomain() }
        }

    override suspend fun cancelListing(id: String, reason: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { listingApi.cancelListing(id, CancelBody(reason)) } }

    override suspend fun getMyListings(type: ListingType, status: ListingStatus?): DataResult<List<ListingCard>> =
        withContext(ioDispatcher) {
            safeApiCall {
                listingApi.getMyListings(type.wire(), status?.wire()).results.map { it.toDomain() }
            }
        }
}
