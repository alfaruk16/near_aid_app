# NearAid — Technical Documentation

Reference docs for the NearAid Android app. Start with the [README](../README.md) for the
product overview; these go one level deeper into how each part is built.

| Doc | Covers |
|---|---|
| [Architecture](architecture.md) | Clean Architecture + MVI, the module graph, `DataResult`/`AppError`, dispatchers, Hilt DI, and the `build-logic` convention plugins |
| [Data, networking, auth & realtime](data-and-networking.md) | Retrofit/OkHttp, API services & DTOs, `safeApiCall`, token attach/refresh, repositories & mappers, the chat WebSocket, Room/DataStore caching, FCM |
| [Navigation](navigation.md) | Type-safe Navigation Compose — the `Routes.kt` registry, the two-level host, bottom nav, and per-feature graphs |
| [Design system & accessibility](design-system-and-accessibility.md) | Reusable Compose components, the semantic color/theme system, and the a11y helpers, tests, and lint rules |
| [AI — on-device semantic search](ai-semantic-search.md) | On-device semantic re-ranking of the discovery feed, the embedder fallback chain, and the model/16 KB setup |
| [Testing](testing.md) | Unit-test stack & patterns, JaCoCo coverage, what's tested per layer, and the hardware-only BLE/benchmark suites |
| [CI/CD](ci-cd.md) | GitHub Actions build/test/coverage/lint and release pipelines |
