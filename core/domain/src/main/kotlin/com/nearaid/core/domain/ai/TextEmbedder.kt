package com.nearaid.core.domain.ai

/**
 * Turns free text into a dense vector ("embedding") so two pieces of text can be
 * compared by *meaning* rather than by shared words. NearAid uses this for on-device
 * semantic matching — e.g. a search for "baby formula" ranks an offer titled
 * "surplus infant milk powder" highly even though no word overlaps.
 *
 * Implementations live behind this interface so the domain layer never depends on a
 * specific ML vendor (MediaPipe / TFLite). The concrete embedder ([com.nearaid.core.ai])
 * runs fully on-device: no text leaves the phone.
 */
interface TextEmbedder {

    /**
     * Embeds [text] into a fixed-length unit-normalized vector. Returns `null` if the
     * model is unavailable (asset missing, init failed) so callers can fall back to the
     * server's distance-based ordering instead of failing the whole feed.
     */
    suspend fun embed(text: String): FloatArray?

    companion object {
        /**
         * Cosine similarity of two embeddings, in [-1, 1] (1 = most similar). Assumes both
         * vectors are the same length; mismatched or empty inputs score 0.
         */
        fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
            if (a.isEmpty() || a.size != b.size) return 0f
            var dot = 0f
            var normA = 0f
            var normB = 0f
            for (i in a.indices) {
                dot += a[i] * b[i]
                normA += a[i] * a[i]
                normB += b[i] * b[i]
            }
            if (normA == 0f || normB == 0f) return 0f
            return dot / (kotlin.math.sqrt(normA) * kotlin.math.sqrt(normB))
        }
    }
}
