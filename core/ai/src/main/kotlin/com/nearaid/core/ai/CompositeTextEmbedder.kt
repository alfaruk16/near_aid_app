package com.nearaid.core.ai

import com.nearaid.core.ai.di.Lexical
import com.nearaid.core.ai.di.Semantic
import com.nearaid.core.domain.ai.TextEmbedder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The [TextEmbedder] bound app-wide. Prefers the on-device [Semantic] model; if that
 * model asset is absent or fails to load — so its [TextEmbedder.embed] returns `null` — it
 * falls back to the dependency-free [Lexical] embedder so search still re-ranks by keyword
 * overlap.
 *
 * Model presence is stable for an app run, so every call resolves to the same underlying
 * embedder — query and listing vectors always come from one space, keeping cosine
 * similarity meaningful.
 */
@Singleton
class CompositeTextEmbedder @Inject constructor(
    @Semantic private val semantic: TextEmbedder,
    @Lexical private val lexical: TextEmbedder,
) : TextEmbedder {

    override suspend fun embed(text: String): FloatArray {
        semantic.embed(text)?.let { return it }
        return lexical.embed(text) ?: error("Lexical embedder must never return null")
    }
}
