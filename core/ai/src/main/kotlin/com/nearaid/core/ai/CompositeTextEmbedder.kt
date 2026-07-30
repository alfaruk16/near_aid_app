package com.nearaid.core.ai

import com.nearaid.core.domain.ai.TextEmbedder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The [TextEmbedder] bound app-wide. Prefers the on-device semantic model
 * ([MediaPipeTextEmbedder]); if that model asset is absent or fails to load — so its
 * [MediaPipeTextEmbedder.embed] returns `null` — it falls back to the dependency-free
 * lexical [HashingTextEmbedder] so search still re-ranks by keyword overlap.
 *
 * Model presence is stable for an app run, so every call resolves to the same underlying
 * embedder — query and listing vectors always come from one space, keeping cosine
 * similarity meaningful.
 */
@Singleton
class CompositeTextEmbedder @Inject constructor(
    private val semantic: MediaPipeTextEmbedder,
    private val lexical: HashingTextEmbedder,
) : TextEmbedder {

    override suspend fun embed(text: String): FloatArray =
        semantic.embed(text) ?: lexical.embed(text)
}
