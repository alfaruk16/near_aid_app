package com.nearaid.core.ai

import com.nearaid.core.domain.ai.TextEmbedder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HashingTextEmbedderTest {

    private val embedder = HashingTextEmbedder()

    @Test
    fun `shared keywords score higher than unrelated text`() = runTest {
        val query = embedder.embed("warm blanket")
        val related = embedder.embed("Warm blanket to give away")
        val unrelated = embedder.embed("Fresh vegetables from my garden")

        val relatedScore = TextEmbedder.cosineSimilarity(query, related)
        val unrelatedScore = TextEmbedder.cosineSimilarity(query, unrelated)

        assertTrue("related ($relatedScore) should beat unrelated ($unrelatedScore)", relatedScore > unrelatedScore)
    }

    @Test
    fun `identical text is maximally similar`() = runTest {
        val a = embedder.embed("infant formula")
        val b = embedder.embed("infant formula")
        assertEquals(1f, TextEmbedder.cosineSimilarity(a, b), 1e-4f)
    }

    @Test
    fun `output is a fixed-length unit vector`() = runTest {
        val v = embedder.embed("some listing title")
        assertEquals(256, v.size)
        assertEquals(1f, TextEmbedder.cosineSimilarity(v, v), 1e-4f) // unit vector ⇒ self-cosine 1
    }
}
