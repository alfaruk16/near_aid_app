package com.nearaid.core.ai

import com.nearaid.core.domain.ai.TextEmbedder
import javax.inject.Inject
import kotlin.math.sqrt

/**
 * A dependency-free, on-device *lexical* embedder using the feature-hashing ("hashing
 * trick") bag-of-words model: each token is hashed into a fixed-length vector and its
 * count accumulated, then the vector is L2-normalized. Cosine similarity over these
 * vectors reduces to shared-word overlap — so a search re-ranks by keyword match.
 *
 * This is intentionally *not* semantic (it won't match "milk" to "formula"). It exists as
 * a zero-setup baseline so the search feature works before the MediaPipe model asset is
 * present. When that asset ships, [MediaPipeTextEmbedder] takes over and upgrades quality;
 * see [CompositeTextEmbedder].
 */
class HashingTextEmbedder @Inject constructor() : TextEmbedder {

    override suspend fun embed(text: String): FloatArray {
        val vec = FloatArray(DIM)
        val tokens = text.lowercase().split(TOKEN_SPLIT).filter { it.isNotBlank() }
        for (token in tokens) {
            val idx = (token.hashCode() % DIM + DIM) % DIM
            vec[idx] += 1f
        }
        var norm = 0f
        for (v in vec) norm += v * v
        if (norm > 0f) {
            val inv = 1f / sqrt(norm)
            for (i in vec.indices) vec[i] *= inv
        }
        return vec
    }

    private companion object {
        const val DIM = 256
        val TOKEN_SPLIT = Regex("[^\\p{L}\\p{N}]+")
    }
}
