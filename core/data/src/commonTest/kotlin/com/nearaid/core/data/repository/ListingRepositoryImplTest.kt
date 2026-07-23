package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.data.mapper.toEntity
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.NewListing
import com.nearaid.core.model.Urgency
import com.nearaid.core.network.api.ListingApi
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ListingRepositoryImplTest {

    private val query = DiscoveryQuery(type = ListingType.REQUEST, lat = 23.7, lng = 90.4)
    private val cardJson = """{"id":"l1","type":"request","title":"Rice","author":{"id":"u1"}}"""

    @Test
    fun getNearby_maps_results_and_caches_the_first_page() = runTest {
        val rec = RecordingRequests()
        val dao = FakeListingCacheDao()
        val api = ListingApi(testClient(rec) { """{"results":[$cardJson],"next_cursor":"n2","has_more":true}""" })
        val repo = ListingRepositoryImpl(api, dao, testDispatcher)

        val result = repo.getNearby(query, cursor = null)
        assertIs<DataResult.Success<*>>(result)
        assertEquals(1, (result as DataResult.Success).data.items.size)
        // First page clears + repopulates the cache for this feed type.
        assertEquals(1, dao.clearByTypeCalls)
        assertEquals(1, dao.store.size)
        assertEquals("request", dao.store.first().feedType)
        // Query forwards the wired type.
        assertEquals("request", rec.requests.last().url.parameters["type"])
    }

    @Test
    fun getNearby_does_not_touch_cache_for_a_paged_request() = runTest {
        val dao = FakeListingCacheDao()
        val api = ListingApi(testClient { """{"results":[$cardJson]}""" })
        val repo = ListingRepositoryImpl(api, dao, testDispatcher)
        repo.getNearby(query, cursor = "page-2")
        assertEquals(0, dao.clearByTypeCalls)
        assertTrue(dao.store.isEmpty())
    }

    @Test
    fun getNearby_falls_back_to_cache_when_the_first_page_fails() = runTest {
        val cached = com.nearaid.core.model.ListingCard(
            id = "cached-1",
            type = ListingType.REQUEST,
            title = "Cached rice",
            category = null,
            urgency = null,
            availableUntil = null,
            quantity = null,
            distanceKm = null,
            areaLabel = null,
            locationFuzzed = null,
            thumbnailUrl = null,
            author = com.nearaid.core.model.Author("u1", null, null, null, false),
            status = ListingStatus.OPEN,
            createdAt = "2026",
        )
        // Seed a cached card for the REQUEST feed.
        val dao = FakeListingCacheDao().apply { store += cached.toEntity("request") }
        val repo = ListingRepositoryImpl(ListingApi(failingClient(HttpStatusCode.InternalServerError)), dao, testDispatcher)
        val result = repo.getNearby(query, cursor = null)
        assertIs<DataResult.Success<*>>(result)
        assertEquals("cached-1", (result as DataResult.Success).data.items.first().id)
        assertEquals(false, result.data.hasMore)
    }

    @Test
    fun getNearby_returns_the_failure_when_cache_is_empty() = runTest {
        val repo = ListingRepositoryImpl(ListingApi(failingClient(HttpStatusCode.InternalServerError)), FakeListingCacheDao(), testDispatcher)
        assertIs<DataResult.Failure>(repo.getNearby(query, cursor = null))
    }

    @Test
    fun getListing_maps_the_detail() = runTest {
        val rec = RecordingRequests()
        val api = ListingApi(testClient(rec) { """{"id":"l1","type":"offer","title":"Rice","author":{"id":"u1"}}""" })
        val repo = ListingRepositoryImpl(api, FakeListingCacheDao(), testDispatcher)
        val result = repo.getListing("l1")
        assertIs<DataResult.Success<*>>(result)
        assertEquals(ListingType.OFFER, (result as DataResult.Success).data.type)
        assertEquals("/listings/l1", rec.lastPath)
    }

    @Test
    fun createListing_maps_input_to_the_wire_body() = runTest {
        val rec = RecordingRequests()
        val api = ListingApi(testClient(rec) { """{"id":"l9","type":"offer","title":"Rice","author":{"id":"u1"}}""" })
        val repo = ListingRepositoryImpl(api, FakeListingCacheDao(), testDispatcher)
        val input = NewListing(
            type = ListingType.OFFER,
            categoryId = 3,
            title = "Rice",
            description = "5kg",
            quantity = null,
            urgency = Urgency.HIGH,
            availableUntil = null,
            lat = 23.7,
            lng = 90.4,
            areaLabel = null,
        )
        val result = repo.createListing(input)
        assertIs<DataResult.Success<*>>(result)
        val body = (rec.requests.last().body as TextContent).text
        assertTrue(body.contains("\"type\":\"offer\""), body)
        assertTrue(body.contains("\"urgency\":\"high\""), body)
        assertTrue(body.contains("\"category_id\":3"), body)
    }

    @Test
    fun cancelListing_posts_the_reason() = runTest {
        val rec = RecordingRequests()
        val repo = ListingRepositoryImpl(ListingApi(testClient(rec) { "" }), FakeListingCacheDao(), testDispatcher)
        assertIs<DataResult.Success<Unit>>(repo.cancelListing("l1", "found elsewhere"))
        assertEquals("/listings/l1/cancel", rec.lastPath)
        assertTrue((rec.requests.last().body as TextContent).text.contains("found elsewhere"))
    }

    @Test
    fun getMyListings_forwards_type_and_status_as_wire_params() = runTest {
        val rec = RecordingRequests()
        val repo = ListingRepositoryImpl(ListingApi(testClient(rec) { """{"results":[]}""" }), FakeListingCacheDao(), testDispatcher)
        repo.getMyListings(ListingType.OFFER, ListingStatus.CLAIMED)
        assertEquals("offer", rec.requests.last().url.parameters["type"])
        assertEquals("claimed", rec.requests.last().url.parameters["status"])
    }
}
