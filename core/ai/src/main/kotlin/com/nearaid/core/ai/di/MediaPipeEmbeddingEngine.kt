package com.nearaid.core.ai.di

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder as MpTextEmbedder
import com.nearaid.core.ai.EmbeddingSession
import com.nearaid.core.ai.EmbeddingSessionFactory

/**
 * Thin native glue over MediaPipe Tasks — the one place that touches the TFLite runtime.
 * Lives in the `di` package so it is excluded from unit-test coverage (it can only be
 * exercised by an instrumented/connected test with the model asset present); all lifecycle
 * logic around it lives in the unit-tested [com.nearaid.core.ai.MediaPipeTextEmbedder].
 */
internal const val MODEL_ASSET = "universal_sentence_encoder.tflite"

/**
 * Builds an [EmbeddingSessionFactory] that loads [MODEL_ASSET] via MediaPipe. Returns a
 * factory whose `create()` yields `null` if the asset is missing or the runtime fails to
 * initialize, matching the contract [com.nearaid.core.ai.MediaPipeTextEmbedder] expects.
 */
internal fun mediaPipeSessionFactory(context: Context) = EmbeddingSessionFactory {
    val options = MpTextEmbedder.TextEmbedderOptions.builder()
        .setBaseOptions(BaseOptions.builder().setModelAssetPath(MODEL_ASSET).build())
        .setL2Normalize(true)
        .build()
    val mp = MpTextEmbedder.createFromOptions(context, options)
    EmbeddingSession { text ->
        mp.embed(text).embeddingResult().embeddings().firstOrNull()?.floatEmbedding()
    }
}
