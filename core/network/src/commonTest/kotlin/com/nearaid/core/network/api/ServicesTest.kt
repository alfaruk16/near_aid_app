package com.nearaid.core.network.api

import com.nearaid.core.network.dto.BlockBody
import com.nearaid.core.network.dto.CancelBody
import com.nearaid.core.network.dto.CreateListingBody
import com.nearaid.core.network.dto.DeviceBody
import com.nearaid.core.network.dto.OtpRequestBody
import com.nearaid.core.network.dto.OtpVerifyBody
import com.nearaid.core.network.dto.PatchMeBody
import com.nearaid.core.network.dto.RatingBody
import com.nearaid.core.network.dto.ReportBody
import com.nearaid.core.network.dto.SendMessageBody
import com.nearaid.core.network.dto.TokenRefreshRequestDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServicesTest {

    // ---- AuthApi ----

    @Test
    fun authApi_verifyOtp_posts_body_and_parses_the_session() = runTest {
        val rec = Recorder()
        val client = mockClient(
            rec,
            responseBody = """{"access_token":"a","refresh_token":"r","is_new_user":true,"user":{"id":"u1"}}""",
        )
        val res = AuthApi(client).verifyOtp(OtpVerifyBody("req", "123456"))
        assertEquals("POST", rec.lastMethod)
        assertEquals("/auth/otp/verify", rec.lastPath)
        assertEquals("a", res.accessToken)
        assertTrue(res.isNewUser)
        assertEquals("u1", res.user.id)
    }

    @Test
    fun authApi_requestOtp_and_refresh_and_logout_hit_the_right_paths() = runTest {
        val rec = Recorder()
        val client = mockClient(rec, responseBody = """{"request_id":"r","expires_in":90}""")
        AuthApi(client).requestOtp(OtpRequestBody("+8801712345678"))
        assertEquals("/auth/otp/request", rec.lastPath)

        val rec2 = Recorder()
        AuthApi(mockClient(rec2, responseBody = """{"access":"newtok"}""")).refresh(TokenRefreshRequestDto("r"))
        assertEquals("/auth/refresh", rec2.lastPath)

        val rec3 = Recorder()
        AuthApi(mockClient(rec3)).logout()
        assertEquals("/auth/logout", rec3.lastPath)
    }

    // ---- UserApi ----

    @Test
    fun userApi_getMe_and_updateMe_target_me() = runTest {
        val me = """{"id":"u1","phone":"+880"}"""
        val rec = Recorder()
        UserApi(mockClient(rec, responseBody = me)).getMe()
        assertEquals("/me", rec.lastPath)
        assertEquals("GET", rec.lastMethod)

        val rec2 = Recorder()
        UserApi(mockClient(rec2, responseBody = me)).updateMe(PatchMeBody(displayName = "Rahim", language = "en"))
        assertEquals("/me", rec2.lastPath)
        assertEquals("PATCH", rec2.lastMethod)
        assertTrue(rec2.lastBody.contains("Rahim"))
    }

    @Test
    fun userApi_getPublicUser_and_getRatings_build_paths_and_cursor() = runTest {
        val rec = Recorder()
        UserApi(mockClient(rec, responseBody = """{"id":"u2"}""")).getPublicUser("u2")
        assertEquals("/users/u2", rec.lastPath)

        val rec2 = Recorder()
        UserApi(mockClient(rec2, responseBody = """{"results":[]}""")).getRatings("u2", "cur-1")
        assertEquals("/users/u2/ratings", rec2.lastPath)
        assertEquals("cur-1", rec2.param("cursor"))
    }

    @Test
    fun userApi_registerDevice_sends_fcm_token() = runTest {
        val rec = Recorder()
        UserApi(mockClient(rec)).registerDevice(DeviceBody("fcm-1"))
        assertEquals("/me/devices", rec.lastPath)
        assertTrue(rec.lastBody.contains("fcm-1"))
    }

    @Test
    fun userApi_submitVerification_posts_multipart_to_verification() = runTest {
        val rec = Recorder()
        UserApi(mockClient(rec)).submitVerification("filebytes".encodeToByteArray(), "id.jpg")
        assertEquals("/me/verification", rec.lastPath)
        assertEquals("POST", rec.lastMethod)
        assertTrue(rec.last.body.contentType?.toString()?.contains("multipart/form-data") == true)
    }

    // ---- CategoryApi ----

    @Test
    fun categoryApi_getCategories_parses_results() = runTest {
        val rec = Recorder()
        val res = CategoryApi(
            mockClient(rec, responseBody = """{"results":[{"id":1,"key":"food","name_en":"Food","name_bn":"খ"}]}"""),
        ).getCategories()
        assertEquals("/categories", rec.lastPath)
        assertEquals(1, res.results.size)
        assertEquals("food", res.results.first().key)
    }

    // ---- ListingApi ----

    @Test
    fun listingApi_getNearby_encodes_all_query_params() = runTest {
        val rec = Recorder()
        ListingApi(mockClient(rec, responseBody = """{"results":[]}""")).getNearby(
            type = "request",
            lat = 23.7,
            lng = 90.4,
            radiusKm = 8.0,
            categories = listOf("food", "medicine"),
            urgency = "high",
            query = "rice",
            cursor = "c1",
        )
        assertEquals("/listings/nearby", rec.lastPath)
        assertEquals("request", rec.param("type"))
        assertEquals("8.0", rec.param("radius_km"))
        assertEquals("high", rec.param("urgency"))
        assertEquals("rice", rec.param("q"))
        assertEquals("c1", rec.param("cursor"))
        assertEquals(listOf("food", "medicine"), rec.last.url.parameters.getAll("category"))
    }

    @Test
    fun listingApi_create_get_cancel_claim_myListings() = runTest {
        val detail = """{"id":"l1","type":"offer","title":"Rice","author":{"id":"u1"}}"""
        val rec = Recorder()
        ListingApi(mockClient(rec, responseBody = detail)).getListing("l1")
        assertEquals("/listings/l1", rec.lastPath)

        val rec2 = Recorder()
        ListingApi(mockClient(rec2, responseBody = detail)).createListing(
            CreateListingBody(type = "offer", categoryId = 3, title = "Rice", lat = 1.0, lng = 2.0),
        )
        assertEquals("/listings", rec2.lastPath)
        assertEquals("POST", rec2.lastMethod)
        assertTrue(rec2.lastBody.contains("\"category_id\":3"))

        val rec3 = Recorder()
        ListingApi(mockClient(rec3)).cancelListing("l1", CancelBody("reason"))
        assertEquals("/listings/l1/cancel", rec3.lastPath)

        val rec4 = Recorder()
        ListingApi(mockClient(rec4, responseBody = """{"claim_id":"c1","listing_id":"l1"}""")).claim("l1")
        assertEquals("/listings/l1/claim", rec4.lastPath)

        val rec5 = Recorder()
        ListingApi(mockClient(rec5, responseBody = """{"results":[]}""")).getMyListings("offer", "open")
        assertEquals("/me/listings", rec5.lastPath)
        assertEquals("offer", rec5.param("type"))
        assertEquals("open", rec5.param("status"))
    }

    // ---- ClaimApi ----

    @Test
    fun claimApi_lifecycle_endpoints() = runTest {
        val rec = Recorder(); ClaimApi(mockClient(rec)).withdraw("c1")
        assertEquals("/claims/c1/withdraw", rec.lastPath)
        val r2 = Recorder(); ClaimApi(mockClient(r2)).deliver("c1")
        assertEquals("/claims/c1/deliver", r2.lastPath)
        val r3 = Recorder(); ClaimApi(mockClient(r3)).confirm("c1")
        assertEquals("/claims/c1/confirm", r3.lastPath)
        val r4 = Recorder(); ClaimApi(mockClient(r4)).markRead("c1")
        assertEquals("/claims/c1/messages/read", r4.lastPath)
    }

    @Test
    fun claimApi_rate_sends_score_and_comment() = runTest {
        val rec = Recorder()
        ClaimApi(mockClient(rec)).rate("c1", RatingBody(5, "great"))
        assertEquals("/claims/c1/rating", rec.lastPath)
        assertTrue(rec.lastBody.contains("\"score\":5"))
        assertTrue(rec.lastBody.contains("great"))
    }

    @Test
    fun claimApi_getMyClaims_and_getMessages_and_sendMessage() = runTest {
        val rec = Recorder()
        ClaimApi(mockClient(rec, responseBody = """{"results":[]}""")).getMyClaims("completed")
        assertEquals("/me/claims", rec.lastPath)
        assertEquals("completed", rec.param("status"))

        val rec2 = Recorder()
        ClaimApi(mockClient(rec2, responseBody = """{"results":[]}""")).getMessages("c1", "cur")
        assertEquals("/claims/c1/messages", rec2.lastPath)
        assertEquals("cur", rec2.param("cursor"))

        val rec3 = Recorder()
        val msg = ClaimApi(mockClient(rec3, responseBody = """{"id":"m1","sender":"u1","body":"hi"}"""))
            .sendMessage("c1", SendMessageBody(body = "hi"))
        assertEquals("/claims/c1/messages", rec3.lastPath)
        assertEquals("m1", msg.id)
        assertTrue(rec3.lastBody.contains("hi"))
    }

    // ---- ChatApi ----

    @Test
    fun chatApi_getConversations_forwards_cursor() = runTest {
        val rec = Recorder()
        ChatApi(mockClient(rec, responseBody = """{"results":[]}""")).getConversations("cur-2")
        assertEquals("/me/conversations", rec.lastPath)
        assertEquals("cur-2", rec.param("cursor"))
    }

    // ---- SafetyApi ----

    @Test
    fun safetyApi_report_block_unblock_getBlocked() = runTest {
        val rec = Recorder()
        SafetyApi(mockClient(rec)).report(ReportBody("listing", "l1", "spam"))
        assertEquals("/reports", rec.lastPath)
        assertTrue(rec.lastBody.contains("spam"))

        val r2 = Recorder()
        SafetyApi(mockClient(r2)).block(BlockBody("u5"))
        assertEquals("/blocks", r2.lastPath)

        val r3 = Recorder()
        SafetyApi(mockClient(r3)).unblock("u6")
        assertEquals("/blocks/u6", r3.lastPath)
        assertEquals("DELETE", r3.lastMethod)

        val r4 = Recorder()
        val blocked = SafetyApi(mockClient(r4, responseBody = """{"results":[{"id":"u2"}]}""")).getBlocked()
        assertEquals("/me/blocks", r4.lastPath)
        assertEquals(1, blocked.results.size)
    }

    // ---- NotificationApi ----

    @Test
    fun notificationApi_get_and_markAllRead() = runTest {
        val rec = Recorder()
        val res = NotificationApi(mockClient(rec, responseBody = """{"results":[{"id":"n1"}]}""")).getNotifications()
        assertEquals("/me/notifications", rec.lastPath)
        assertEquals(1, res.results.size)

        val r2 = Recorder()
        NotificationApi(mockClient(r2)).markAllRead()
        assertEquals("/me/notifications/read", r2.lastPath)
    }
}
