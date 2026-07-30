package com.nearaid.core.ai

import com.nearaid.core.domain.ai.TextEmbedder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CompositeTextEmbedderTest {

    /** Records every text it sees, and returns [result] (possibly null) for each call. */
    private class RecordingEmbedder(private val result: FloatArray?) : TextEmbedder {
        val seen = mutableListOf<String>()
        override suspend fun embed(text: String): FloatArray? {
            seen += text
            return result
        }
    }

    @Test
    fun `uses the semantic embedder when it returns a vector`() = runTest {
        val semanticVec = floatArrayOf(1f, 2f, 3f)
        val semantic = RecordingEmbedder(semanticVec)
        val lexical = RecordingEmbedder(floatArrayOf(9f))
        val composite = CompositeTextEmbedder(semantic, lexical)

        val result = composite.embed("baby formula")

        assertArrayEquals(semanticVec, result, 0f)
        assertEquals(listOf("baby formula"), semantic.seen)
        assertEquals("lexical must not be consulted when semantic succeeds", emptyList<String>(), lexical.seen)
    }

    @Test
    fun `falls back to the lexical embedder when semantic returns null`() = runTest {
        val semantic = RecordingEmbedder(null)
        val lexicalVec = floatArrayOf(4f, 5f)
        val lexical = RecordingEmbedder(lexicalVec)
        val composite = CompositeTextEmbedder(semantic, lexical)

        val result = composite.embed("warm blanket")

        assertArrayEquals(lexicalVec, result, 0f)
        assertEquals(listOf("warm blanket"), semantic.seen)
        assertEquals(listOf("warm blanket"), lexical.seen)
    }

    @Test(expected = IllegalStateException::class)
    fun `throws when both embedders return null`() = runTest {
        val composite = CompositeTextEmbedder(RecordingEmbedder(null), RecordingEmbedder(null))
        composite.embed("anything")
    }

    @Test
    fun `end-to-end fallback with the real lexical embedder ranks by keyword`() = runTest {
        // Semantic model absent (null) → composite must behave like the lexical embedder.
        val composite = CompositeTextEmbedder(RecordingEmbedder(null), HashingTextEmbedder())

        val query = composite.embed("warm blanket")
        val related = composite.embed("Warm blanket to give")
        val unrelated = composite.embed("Fresh vegetables")

        val relatedScore = TextEmbedder.cosineSimilarity(query, related)
        val unrelatedScore = TextEmbedder.cosineSimilarity(query, unrelated)
        assert(relatedScore > unrelatedScore) { "related=$relatedScore unrelated=$unrelatedScore" }
    }
}
