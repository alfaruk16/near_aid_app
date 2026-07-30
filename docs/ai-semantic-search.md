# AI — On-Device Semantic Search

NearAid re-ranks the discovery feed by **meaning**, entirely on-device — a search for
*"baby formula"* surfaces an offer titled *"surplus infant milk powder"* even with no shared
words. Nothing leaves the phone, which suits a privacy-sensitive mutual-aid app.

It is a **presentation-order layer** on top of the existing distance-based feed: the server
query is unchanged, so the feature degrades gracefully and needs no backend change.

```
GetNearbyListingsUseCase ──► RankListingsBySimilarityUseCase ──► HomeState.listings
   (server, distance)            (:core:ai, on-device)               (UI)
```

## Modules

| Module | Type | Holds |
|---|---|---|
| `:core:domain` | interface + use case | `TextEmbedder` (vendor-neutral) + `cosineSimilarity`; `RankListingsBySimilarityUseCase` |
| `:core:ai` | implementation | `MediaPipeTextEmbedder`, `HashingTextEmbedder`, `CompositeTextEmbedder`, DI wiring |
| `:feature:discovery` | UI | search box, debounce, scroll-to-top, paging in `HomeViewModel`/`HomeScreen` |

`:core:domain` depends only on the `TextEmbedder` **interface** — never on MediaPipe. The
concrete embedders live in `:core:ai`, bound into the graph by `app` depending on `:core:ai`.

## How matching works

1. Each listing's `title` (+ category names) and the search query are turned into a
   fixed-length **embedding** vector by a `TextEmbedder`.
2. Listings are ranked by **cosine similarity** to the query
   (`TextEmbedder.cosineSimilarity`, in `[-1, 1]`).
3. `RankListingsBySimilarityUseCase` caches embeddings by listing id (access-ordered LRU
   `LinkedHashMap`, 256 entries) so keystrokes don't re-encode the same cards. A blank query — or an unavailable
   model — returns the list unchanged, preserving the server's distance order.

## Embedder chain

`CompositeTextEmbedder` is the app-facing `TextEmbedder`. It prefers the semantic model and
falls back to the lexical baseline, so search works with **zero setup** and auto-upgrades
when the model ships.

| Embedder | Backing | Matches by | Needs |
|---|---|---|---|
| `MediaPipeTextEmbedder` (`@Semantic`) | MediaPipe Tasks · multilingual USE (TFLite) | **meaning** (EN↔BN cross-match) | model asset present |
| `HashingTextEmbedder` (`@Lexical`) | pure Kotlin, feature-hashing bag-of-words (dim 256) | **keyword overlap** | nothing |
| `CompositeTextEmbedder` | the two above | semantic if available, else lexical | — |

Fallback order per call: **semantic model → lexical → (blank query) distance order**. Model
presence is stable for an app run, so query and listing vectors always come from one space,
keeping cosine similarity meaningful.

### Testability seam

`MediaPipeTextEmbedder` holds only the **lifecycle logic** — lazy + thread-safe model load,
init-failure latching, and `embed`-error → `null` handling — behind a tiny seam:

```kotlin
fun interface EmbeddingSessionFactory { fun create(): EmbeddingSession? }  // null if unavailable
fun interface EmbeddingSession        { fun embed(text: String): FloatArray? }
```

The single place that touches native MediaPipe/TFLite is `di/MediaPipeEmbeddingEngine.kt`
(`mediaPipeSessionFactory`), provided via `AiModule`. It lives under `di/`, which the
[JaCoCo convention](../build-logic/convention/src/main/kotlin/Jacoco.kt) excludes, so
untestable native glue doesn't count against coverage while the surrounding logic is fully
unit-tested with a fake factory.

## Enabling semantic matching (model asset)

Until the model is bundled, search runs on the **lexical** fallback (keyword match). To turn
on true semantic matching:

1. Download the **multilingual** Universal Sentence Encoder from MediaPipe's model catalog.
2. Drop it at `core/ai/src/main/assets/universal_sentence_encoder.tflite`
   (see `MODEL_ASSET` in `di/MediaPipeEmbeddingEngine.kt`).

No code change — `MediaPipeTextEmbedder` loads it lazily on first use; if the load fails it
latches `initFailed` and never retries, so the feed silently stays on the lexical baseline.

> **16 KB page size:** MediaPipe is pinned to **0.10.26.1**, whose native `.so` is 16 KB
> ELF-aligned (Google Play requirement). Older `0.10.x` builds trip the "app isn't 16 KB
> compatible" dialog on Android 15+ devices.

## Discovery-feed integration (`:feature:discovery`)

`HomeViewModel` keeps the server-ordered page in `sourceListings` and re-ranks on top:

| Intent | Behaviour |
|---|---|
| `SearchChanged(query)` | updates `searchQuery`, re-ranks after a **250 ms debounce** (`SEARCH_DEBOUNCE_MS`); a new keystroke cancels the in-flight `rankJob` |
| `LoadMore` | fetches the next page by `nextCursor`, **appends** to `sourceListings`, re-ranks the accumulated set |

- **Scroll-to-top:** a settled *search* re-rank emits `HomeEffect.ScrollToTop`; `HomeScreen`
  scrolls to index 0 so the new best match is visible. It is an **effect** (emitted *after*
  the reordered list is published), not a state-keyed `LaunchedEffect` — otherwise it would
  race the debounce and scroll the old order, leaving new matches hidden above the viewport.
- **Paging vs. search:** scroll-to-top fires only for search (not pagination), so appending a
  page never yanks the feed upward.

## Testing

- `RankListingsBySimilarityUseCaseTest` — ranking order, blank-query and model-unavailable
  fallbacks, cosine math.
- `HashingTextEmbedderTest` — keyword overlap, case/punctuation/word-order invariance,
  Bengali/unicode tokens, blank → zero vector.
- `CompositeTextEmbedderTest` — semantic wins when non-null (lexical not consulted), fallback
  when null, throws if both null, end-to-end lexical ranking.
- `MediaPipeTextEmbedderTest` — success, unavailable/throwing model → `null`, model loaded
  **once**, no retry after init failure (via a fake `EmbeddingSessionFactory`).
- `HomeViewModelTest` — search + paging (`LoadMore` appends with cursor; no-op when no next
  page).

`:core:ai` sits at **100 % line coverage** (`./gradlew :core:ai:jacocoTestReport`).
