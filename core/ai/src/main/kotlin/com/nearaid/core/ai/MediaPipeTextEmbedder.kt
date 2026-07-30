package com.nearaid.core.ai

import com.nearaid.core.common.dispatcher.Dispatcher
import com.nearaid.core.common.dispatcher.NearAidDispatcher
import com.nearaid.core.domain.ai.TextEmbedder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A loaded native embedding model. [embed] may throw; callers guard it. Implemented in the
 * `di` package by the MediaPipe-backed engine (excluded from coverage as native glue).
 */
fun interface EmbeddingSession {
    fun embed(text: String): FloatArray?
}

/**
 * Loads an [EmbeddingSession], or returns `null` when the model is unavailable (asset
 * missing / init failed). This is the single seam over native MediaPipe/TFLite calls, so
 * the surrounding lifecycle logic in [MediaPipeTextEmbedder] is unit-testable with a fake.
 */
fun interface EmbeddingSessionFactory {
    fun create(): EmbeddingSession?
}

/**
 * On-device [TextEmbedder] backed by a native embedding model. Loads the model lazily and
 * thread-safely on first use ([EmbeddingSessionFactory.create]) and reuses it for the app's
 * lifetime. If the model is unavailable — the TFLite asset isn't bundled yet, or init/embed
 * fails — [embed] returns `null` so callers fall back to distance ordering.
 *
 * The multilingual Universal Sentence Encoder embeds English and Bengali into one space, so
 * a bn query can match an en listing and vice-versa.
 *
 * ⚠️ Setup: drop `universal_sentence_encoder.tflite` (multilingual) into
 * `core/ai/src/main/assets/` to enable semantic (vs lexical) matching.
 */
@Singleton
class MediaPipeTextEmbedder @Inject constructor(
    private val sessionFactory: EmbeddingSessionFactory,
    @Dispatcher(NearAidDispatcher.Default) private val dispatcher: CoroutineDispatcher,
) : TextEmbedder {

    private val initMutex = Mutex()

    @Volatile
    private var session: EmbeddingSession? = null

    @Volatile
    private var initFailed = false

    override suspend fun embed(text: String): FloatArray? = withContext(dispatcher) {
        val active = ensureSession() ?: return@withContext null
        runCatching { active.embed(text) }.getOrNull()
    }

    private suspend fun ensureSession(): EmbeddingSession? {
        session?.let { return it }
        if (initFailed) return null
        return initMutex.withLock {
            session?.let { return it }
            if (initFailed) return null
            val created = runCatching { sessionFactory.create() }.getOrNull()
            if (created == null) initFailed = true else session = created
            created
        }
    }
}
