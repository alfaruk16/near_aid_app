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

    @Test
    fun `blank or empty text yields a fixed-length zero vector`() = runTest {
        for (text in listOf("", "   ", "\t\n", "!!! ,. ---")) {
            val v = embedder.embed(text)
            assertEquals(256, v.size)
            assertTrue("expected all-zero vector for '$text'", v.all { it == 0f })
            // cosine against a real vector is defined as 0 for a zero vector
            val real = embedder.embed("blanket")
            assertEquals(0f, TextEmbedder.cosineSimilarity(v, real), 1e-4f)
        }
    }

    @Test
    fun `embedding is case-insensitive`() = runTest {
        val lower = embedder.embed("infant formula")
        val upper = embedder.embed("INFANT FORMULA")
        assertEquals(1f, TextEmbedder.cosineSimilarity(lower, upper), 1e-4f)
    }

    @Test
    fun `punctuation and extra whitespace do not change the embedding`() = runTest {
        val plain = embedder.embed("warm blanket")
        val messy = embedder.embed("  warm,  blanket!! ")
        assertEquals(1f, TextEmbedder.cosineSimilarity(plain, messy), 1e-4f)
    }

    @Test
    fun `bag-of-words is word-order independent`() = runTest {
        val ab = embedder.embed("warm blanket")
        val ba = embedder.embed("blanket warm")
        assertEquals(1f, TextEmbedder.cosineSimilarity(ab, ba), 1e-4f)
    }

    @Test
    fun `bengali tokens are embedded and match themselves`() = runTest {
        // "কম্বল" (blanket) — non-Latin script must still tokenize and rank.
        val query = embedder.embed("কম্বল")
        val related = embedder.embed("আমার একটি কম্বল আছে") // "I have a blanket"
        val unrelated = embedder.embed("তাজা সবজি")          // "fresh vegetables"

        val relatedScore = TextEmbedder.cosineSimilarity(query, related)
        val unrelatedScore = TextEmbedder.cosineSimilarity(query, unrelated)
        assertTrue("shared token should score > 0 (was $relatedScore)", relatedScore > 0f)
        assertTrue("related ($relatedScore) should beat unrelated ($unrelatedScore)", relatedScore > unrelatedScore)
    }
}
