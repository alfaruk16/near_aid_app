package com.nearaid.core.ai

import android.content.Context
import com.google.mediapipe.tasks.components.containers.Embedding
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder as MpTextEmbedder
import com.nearaid.core.common.dispatcher.Dispatcher
import com.nearaid.core.common.dispatcher.NearAidDispatcher
import com.nearaid.core.domain.ai.TextEmbedder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device [TextEmbedder] backed by MediaPipe Tasks. Loads a bundled TFLite model from
 * assets ([MODEL_ASSET]) lazily on first use and reuses it for the app's lifetime.
 *
 * We use the **multilingual** Universal Sentence Encoder so English and Bengali listing
 * text embed into the same space (a bn query can match an en offer, and vice-versa).
 *
 * ⚠️ Setup: drop `universal_sentence_encoder.tflite` (multilingual variant) into
 * `core/ai/src/main/assets/`. Download from MediaPipe's model catalog. Until the asset is
 * present, [embed] returns `null` and callers fall back to distance ordering — the feed
 * still works, it just isn't semantically re-ranked.
 */
@Singleton
class MediaPipeTextEmbedder @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(NearAidDispatcher.Default) private val dispatcher: CoroutineDispatcher,
) : TextEmbedder {

    private val initMutex = Mutex()

    @Volatile
    private var delegate: MpTextEmbedder? = null

    @Volatile
    private var initFailed = false

    override suspend fun embed(text: String): FloatArray? = withContext(dispatcher) {
        val embedder = ensureEmbedder() ?: return@withContext null
        runCatching {
            val result = embedder.embed(text)
            result.embeddingResult().embeddings().firstOrNull()?.toFloatArray()
        }.getOrNull()
    }

    private suspend fun ensureEmbedder(): MpTextEmbedder? {
        delegate?.let { return it }
        if (initFailed) return null
        return initMutex.withLock {
            delegate?.let { return it }
            if (initFailed) return null
            runCatching {
                val options = MpTextEmbedder.TextEmbedderOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setModelAssetPath(MODEL_ASSET).build())
                    .setL2Normalize(true)
                    .build()
                MpTextEmbedder.createFromOptions(context, options)
            }.onSuccess { delegate = it }
                .onFailure { initFailed = true }
                .getOrNull()
        }
    }

    private fun Embedding.toFloatArray(): FloatArray = floatEmbedding()

    private companion object {
        const val MODEL_ASSET = "universal_sentence_encoder.tflite"
    }
}
