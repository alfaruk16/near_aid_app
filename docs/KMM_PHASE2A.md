# KMM Phase 2a — Retrofit→Ktor + DataStore-KMP (done)

Part of the [KMM migration roadmap](KMM_MIGRATION_ROADMAP.md). Phase 2 ("share data access") is
sliced; **2a moves `:core:network` (transport) and `:core:datastore` (key-value) into `commonMain`**.
2b = Room-KMP (`:core:database`), 2c = `:core:data` → commonMain.

**Why DataStore ships with network:** the Ktor `Auth` plugin and `ChatSocket` reference
`AuthPreferencesDataSource` in commonMain, and a commonMain module can't depend on an Android-only
module — so `:core:datastore` had to become KMP together with network. Secure token storage is
**deferred** (tokens remain plain, same as before).

## What changed
- **Retrofit + OkHttp → Ktor 3.0.3** (`ktor-client-core/-content-negotiation/-serialization-kotlinx-json/-auth/-logging/-websockets`); engines `ktor-client-okhttp` (androidMain) + `ktor-client-darwin` (iosMain), auto-selected.
- **8 API interfaces → Ktor client classes** (same class/method names → `:core:data` repos untouched). All ~33 endpoints ported; JSON bodies send `contentType(Application.Json)` so ContentNegotiation serializes them.
- **`AuthInterceptor` + `TokenAuthenticator` → a Ktor `Auth { bearer { loadTokens / refreshTokens } }`** block (`sendWithoutRequest` skips `/auth/`). Removes the old OkHttp↔Retrofit↔AuthApi cycle; Ktor queues concurrent 401s.
- **`ChatSocket` → Ktor `WebSockets`** (`webSocketSession` + `incoming` frames), same `message.new` parsing, no `runBlocking`.
- **`safeApiCall`** rewritten for Ktor: `expectSuccess = true` → catch `ResponseException` → suspend `HttpResponse.toAppError()` decoding the `{"error":{…}}` envelope (same `AppError` mapping); connectivity failures → `AppError.Network`.
- **Multipart** (`UserApi.submitVerification`) now takes `ByteArray + fileName` (`MultiPartFormDataContent`); `UserRepositoryImpl` reads the file→bytes.
- **`:core:datastore` → DataStore-KMP** (`datastore-preferences-core` + `okio`): data sources unchanged; the `DataStore<Preferences>` path is provided per-platform via `expect val dataStorePlatformModule` (Android `filesDir`, iOS `NSDocumentDirectory` through `createWithPath`).
- `NetworkConfig` gained `debugLogging` (replaces `BuildConfig.DEBUG`); `:app` passes it.

## Verified — both platforms
- `:app:assembleDebug` + `testDebugUnitTest` green (incl. new Ktor `MockEngine` tests: error-envelope→`AppError` mapping and JSON-body serialization guard).
- `:core:{datastore,network}:compileKotlinIosSimulatorArm64` green; `:core:network:iosSimulatorArm64Test` green (Native).
- On an emulator: app boots (DataStore-KMP read of `isLoggedIn` works), and submitting a phone number fires a **real Ktor request** to `10.0.2.2:8000` → with no local backend it surfaces a graceful "Failed to connect" (`AppError.Network`), no crash — proving the OkHttp engine + Auth plugin + ContentNegotiation pipeline end-to-end.

## Notes
- One bug caught by the device run: Ktor won't serialize `setBody(dto)` unless `contentType(Application.Json)` is set — fixed on every JSON-body call and now covered by `ApiClientTest`.

## Next
2b — `:core:database` → Room-KMP (bundled SQLite driver, per-target KSP, `@ConstructedBy`). Then 2c — `:core:data` → commonMain.
