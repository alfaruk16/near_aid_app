package com.nearaid.core.domain.usecase

import com.nearaid.core.domain.ai.TextEmbedder
import com.nearaid.core.model.ListingCard
import javax.inject.Inject

/**
 * Re-ranks an already-fetched page of nearby listings by *semantic* similarity to a
 * free-text [query], on-device. This is a presentation-order layer on top of
 * [GetNearbyListingsUseCase] — it never changes which listings are fetched, so if the
 * embedder is unavailable the caller keeps the server's distance-based order untouched.
 *
 * Each card is embedded from its "title. description-ish" text (the card only carries a
 * title, so we lean on that plus category name). Embeddings are cached by listing id to
 * avoid re-encoding the same card across keystrokes.
 */
class RankListingsBySimilarityUseCase @Inject constructor(
    private val embedder: TextEmbedder,
) {
    private val cache = object : LinkedHashMap<String, FloatArray>(64, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<String, FloatArray>?): Boolean = size > MAX_CACHE
    }

    /**
     * Returns [listings] reordered by descending similarity to [query]. A blank query, an
     * empty list, or an unavailable model returns [listings] unchanged.
     */
    suspend operator fun invoke(query: String, listings: List<ListingCard>): List<ListingCard> {
        if (query.isBlank() || listings.isEmpty()) return listings
        val queryVec = embedder.embed(query) ?: return listings

        val scored = listings.map { card ->
            val vec = cache[card.id] ?: embedder.embed(card.searchableText())?.also { cache[card.id] = it }
            val score = if (vec != null) TextEmbedder.cosineSimilarity(queryVec, vec) else Float.NEGATIVE_INFINITY
            card to score
        }
        return scored.sortedByDescending { it.second }.map { it.first }
    }

    private fun ListingCard.searchableText(): String =
        listOfNotNull(title, category?.nameEn, category?.nameBn).joinToString(". ")

    private companion object {
        const val MAX_CACHE = 256
    }
}
