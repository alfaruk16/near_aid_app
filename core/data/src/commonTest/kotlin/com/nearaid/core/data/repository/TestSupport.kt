package com.nearaid.core.data.repository

import com.nearaid.core.database.dao.ConversationCacheDao
import com.nearaid.core.database.dao.ListingCacheDao
import com.nearaid.core.database.entity.CachedConversationEntity
import com.nearaid.core.database.entity.CachedListingEntity
import com.nearaid.core.datastore.SecureTokenStore
import com.nearaid.core.datastore.StoredSession
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/** Records every request the API layer made so tests can assert path/method/body. */
class RecordingRequests {
    val requests = mutableListOf<HttpRequestData>()
    val paths: List<String> get() = requests.map { it.url.encodedPath }
    val lastPath: String? get() = paths.lastOrNull()
}

/**
 * Builds an [HttpClient] backed by [MockEngine] that mirrors the production client config
 * (`expectSuccess = true` so non-2xx becomes a `ResponseException` that `safeApiCall` maps).
 * [handler] returns the JSON body for each request; return `null` to emit a 500.
 */
fun testClient(
    recorder: RecordingRequests = RecordingRequests(),
    status: HttpStatusCode = HttpStatusCode.OK,
    handler: (HttpRequestData) -> String?,
): HttpClient {
    val engine = MockEngine { request ->
        recorder.requests += request
        val body = handler(request)
        if (body == null) {
            respondError(HttpStatusCode.InternalServerError)
        } else {
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
    }
    return HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
}

/** A client that fails every request with [status] (default 404). */
fun failingClient(
    status: HttpStatusCode = HttpStatusCode.NotFound,
    recorder: RecordingRequests = RecordingRequests(),
): HttpClient {
    val engine = MockEngine { request ->
        recorder.requests += request
        respondError(status)
    }
    return HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
}

val testDispatcher = Dispatchers.Unconfined

// --- Fake DAOs --------------------------------------------------------------

class FakeListingCacheDao : ListingCacheDao {
    val store = mutableListOf<CachedListingEntity>()
    var clearByTypeCalls = 0

    override suspend fun getByType(type: String): List<CachedListingEntity> =
        store.filter { it.feedType == type }

    override suspend fun upsertAll(items: List<CachedListingEntity>) {
        store += items
    }

    override suspend fun clearByType(type: String) {
        clearByTypeCalls++
        store.removeAll { it.feedType == type }
    }
}

class FakeConversationCacheDao : ConversationCacheDao {
    val store = mutableListOf<CachedConversationEntity>()
    var clearCalls = 0

    override suspend fun getAll(): List<CachedConversationEntity> = store.toList()

    override suspend fun upsertAll(items: List<CachedConversationEntity>) {
        store += items
    }

    override suspend fun clear() {
        clearCalls++
        store.clear()
    }
}

class FakeSecureTokenStore(initial: StoredSession? = null) : SecureTokenStore {
    var session: StoredSession? = initial
    var clearCalls = 0

    override fun readSession(): StoredSession? = session
    override fun save(session: StoredSession) {
        this.session = session
    }

    override fun updateAccessToken(accessToken: String) {
        session = session?.copy(accessToken = accessToken)
    }

    override fun clear() {
        clearCalls++
        session = null
    }
}
