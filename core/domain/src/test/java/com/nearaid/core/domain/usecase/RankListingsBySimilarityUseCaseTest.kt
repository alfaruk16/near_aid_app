package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.ai.TextEmbedder
import com.nearaid.core.model.Author
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RankListingsBySimilarityUseCaseTest {

    /**
     * Fake embedder mapping known phrases to hand-picked 2-D vectors so similarity is
     * deterministic. "milk"/"formula" cluster on one axis, "blanket" on the other.
     */
    private val fakeEmbedder = object : TextEmbedder {
        override suspend fun embed(text: String): FloatArray? {
            val t = text.lowercase()
            return when {
                "milk" in t || "formula" in t -> floatArrayOf(1f, 0f)
                "blanket" in t || "warm" in t -> floatArrayOf(0f, 1f)
                else -> floatArrayOf(0.5f, 0.5f)
            }
        }
    }

    private val nullEmbedder = object : TextEmbedder {
        override suspend fun embed(text: String): FloatArray? = null
    }

    private fun card(id: String, title: String) = ListingCard(
        id = id,
        type = ListingType.OFFER,
        title = title,
        category = null,
        urgency = null,
        availableUntil = null,
        quantity = null,
        distanceKm = 1.0,
        areaLabel = null,
        locationFuzzed = null,
        thumbnailUrl = null,
        author = Author("u1", "Neighbour", null, 4.5, false),
        status = ListingStatus.OPEN,
        createdAt = "2026-07-30T00:00:00Z",
    )

    private val listings = listOf(
        card("1", "Warm blanket to give"),
        card("2", "Surplus infant formula"),
        card("3", "Assorted household items"),
    )

    @Test
    fun `ranks the semantically closest listing first`() = runTest {
        val useCase = RankListingsBySimilarityUseCase(fakeEmbedder)
        val ranked = useCase("need baby milk", listings)
        assertEquals("2", ranked.first().id) // "formula" beats blanket/misc
    }

    @Test
    fun `blank query preserves original order`() = runTest {
        val useCase = RankListingsBySimilarityUseCase(fakeEmbedder)
        val ranked = useCase("   ", listings)
        assertEquals(listOf("1", "2", "3"), ranked.map { it.id })
    }

    @Test
    fun `unavailable model falls back to original order`() = runTest {
        val useCase = RankListingsBySimilarityUseCase(nullEmbedder)
        val ranked = useCase("need baby milk", listings)
        assertEquals(listOf("1", "2", "3"), ranked.map { it.id })
    }

    @Test
    fun `cosine similarity is 1 for identical vectors and 0 for orthogonal`() {
        val v = floatArrayOf(0.3f, 0.7f)
        assertEquals(1f, TextEmbedder.cosineSimilarity(v, v), 1e-4f)
        assertEquals(0f, TextEmbedder.cosineSimilarity(floatArrayOf(1f, 0f), floatArrayOf(0f, 1f)), 1e-4f)
    }
}
