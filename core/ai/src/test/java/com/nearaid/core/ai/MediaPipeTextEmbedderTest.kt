package com.nearaid.core.ai

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaPipeTextEmbedderTest {

    private val dispatcher = UnconfinedTestDispatcher()

    /** Session returning a fixed vector, or throwing if [throwOnEmbed]. Counts embed calls. */
    private class FakeSession(
        private val vector: FloatArray? = floatArrayOf(0.1f, 0.2f),
        private val throwOnEmbed: Boolean = false,
    ) : EmbeddingSession {
        var embedCalls = 0
        override fun embed(text: String): FloatArray? {
            embedCalls++
            if (throwOnEmbed) throw IllegalStateException("native embed failed")
            return vector
        }
    }

    /** Factory that hands out [session], or fails per [mode]. Counts create() calls. */
    private class FakeFactory(
        private val session: EmbeddingSession? = FakeSession(),
        private val mode: Mode = Mode.OK,
    ) : EmbeddingSessionFactory {
        enum class Mode { OK, RETURN_NULL, THROW }
        var createCalls = 0
        override fun create(): EmbeddingSession? {
            createCalls++
            return when (mode) {
                Mode.OK -> session
                Mode.RETURN_NULL -> null
                Mode.THROW -> throw RuntimeException("model asset missing")
            }
        }
    }

    private fun embedder(factory: EmbeddingSessionFactory) =
        MediaPipeTextEmbedder(sessionFactory = factory, dispatcher = dispatcher)

    @Test
    fun `returns the session vector on success`() = runTest(dispatcher) {
        val vec = floatArrayOf(1f, 2f, 3f)
        val result = embedder(FakeFactory(FakeSession(vector = vec))).embed("hello")
        assertArrayEquals(vec, result, 0f)
    }

    @Test
    fun `returns null when the model is unavailable`() = runTest(dispatcher) {
        val result = embedder(FakeFactory(mode = FakeFactory.Mode.RETURN_NULL)).embed("hello")
        assertNull(result)
    }

    @Test
    fun `returns null when creating the session throws`() = runTest(dispatcher) {
        val result = embedder(FakeFactory(mode = FakeFactory.Mode.THROW)).embed("hello")
        assertNull(result)
    }

    @Test
    fun `returns null when the session embed throws`() = runTest(dispatcher) {
        val result = embedder(FakeFactory(FakeSession(throwOnEmbed = true))).embed("hello")
        assertNull(result)
    }

    @Test
    fun `loads the model once and reuses it across calls`() = runTest(dispatcher) {
        val factory = FakeFactory(FakeSession())
        val embedder = embedder(factory)

        embedder.embed("a")
        embedder.embed("b")
        embedder.embed("c")

        assertEquals("session should be created exactly once", 1, factory.createCalls)
    }

    @Test
    fun `does not retry model init after it fails once`() = runTest(dispatcher) {
        val factory = FakeFactory(mode = FakeFactory.Mode.RETURN_NULL)
        val embedder = embedder(factory)

        assertNull(embedder.embed("a"))
        assertNull(embedder.embed("b"))

        assertEquals("failed init must not be retried on every call", 1, factory.createCalls)
    }
}
