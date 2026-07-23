package com.nearaid.core.data.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.api.ListingApi
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ClaimRepositoryImplTest {

    private fun repo(rec: RecordingRequests, body: (io.ktor.client.request.HttpRequestData) -> String?) =
        ClaimRepositoryImpl(
            listingApi = ListingApi(testClient(rec, handler = body)),
            claimApi = ClaimApi(testClient(rec, handler = body)),
            ioDispatcher = testDispatcher,
        )

    @Test
    fun claim_posts_to_the_listing_claim_endpoint_and_maps_the_claim() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec) { """{"claim_id":"c1","listing_id":"l1","status":"active"}""" }
        val result = repo.claim("l1")
        assertIs<DataResult.Success<*>>(result)
        assertEquals("c1", (result as DataResult.Success).data.id)
        assertEquals("/listings/l1/claim", rec.lastPath)
    }

    @Test
    fun withdraw_deliver_confirm_hit_their_endpoints() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec) { "" }
        repo.withdraw("c1")
        assertEquals("/claims/c1/withdraw", rec.lastPath)
        repo.markDelivered("c2")
        assertEquals("/claims/c2/deliver", rec.lastPath)
        repo.confirmReceipt("c3")
        assertEquals("/claims/c3/confirm", rec.lastPath)
    }

    @Test
    fun rate_sends_score_and_comment_in_the_body() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec) { "" }
        repo.rate("c1", 5, "great")
        assertEquals("/claims/c1/rating", rec.lastPath)
        val body = (rec.requests.last().body as TextContent).text
        assertTrue(body.contains("\"score\":5"), body)
        assertTrue(body.contains("great"), body)
    }

    @Test
    fun getMyClaims_forwards_status_as_a_wire_query_param() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec) { """{"results":[{"claim_id":"c1","listing_id":"l1"}]}""" }
        val result = repo.getMyClaims(ClaimStatus.COMPLETED)
        assertIs<DataResult.Success<*>>(result)
        assertEquals(1, (result as DataResult.Success).data.size)
        assertEquals("completed", rec.requests.last().url.parameters["status"])
    }

    @Test
    fun getMyClaims_omits_the_status_param_when_null() = runTest {
        val rec = RecordingRequests()
        val repo = repo(rec) { """{"results":[]}""" }
        repo.getMyClaims(null)
        assertEquals(null, rec.requests.last().url.parameters["status"])
    }
}
