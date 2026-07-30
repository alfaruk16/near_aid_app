# Data, Networking, Auth & Realtime

How NearAid talks to the backend, keeps sessions alive, streams chat, and caches for offline.
See [architecture.md](architecture.md) for the layering these pieces sit in.

## Networking (`:core:network`)

`NetworkModule` (`di/NetworkModule.kt`) provides everything as `@Singleton`:

- **JSON** — kotlinx.serialization `Json` (`ignoreUnknownKeys`, `coerceInputValues`,
  `explicitNulls = false`, `isLenient`), wired to Retrofit via the kotlinx converter factory.
- **OkHttpClient** — in order: `AuthInterceptor` → `HttpLoggingInterceptor` →
  `.authenticator(TokenAuthenticator)`, 30 s connect/read timeouts.
- **Logging** — `Level.BODY` in debug else `NONE`; `Authorization` and `Cookie` headers redacted.
- **Retrofit** — base URL from `@BaseUrl`; creates 8 API interfaces.

Base/WS URLs are `BuildConfig` fields provided in `app/di/AppModule.kt` behind `@BaseUrl` /
`@WsUrl` qualifiers (`di/Qualifiers.kt`):

| Build type | `BASE_URL` | `WS_URL` |
|---|---|---|
| debug | `http://10.0.2.2:8000/v1/` | `ws://10.0.2.2:8000/ws` |
| release | `https://api.nearaid.app/v1/` | `wss://api.nearaid.app/ws` |

### API services & DTOs

All service interfaces live in `api/Services.kt` (every method `suspend`): `AuthApi`,
`UserApi`, `CategoryApi`, `ListingApi`, `ClaimApi`, `ChatApi`, `SafetyApi`, `NotificationApi`.
Wire models are `@Serializable` `@SerialName` snake_case DTOs in `dto/Dtos.kt`.

### Error handling — `safeApiCall`

`util/SafeApiCall.kt` wraps every call: rethrows `CancellationException`, maps `HttpException`
via `toAppError()` (decodes the `{"error":{code,message,details}}` envelope and maps status →
`AppError`), `IOException` → `AppError.Network`, anything else → `AppError.Unknown`. The result
is the `DataResult<T>` the domain layer expects.

## Auth & token refresh

| Piece | File | Role |
|---|---|---|
| Token model | `core/model/AuthTokens.kt` | `AuthTokens(accessToken, refreshToken)`; `AuthSession`, `OtpChallenge` in `Models.kt` |
| Storage | `core/datastore/AuthPreferencesDataSource.kt` | `access_token`/`refresh_token`/`user_id`; `tokens`, `isLoggedIn` flows; `saveSession`, `updateAccessToken`, `clear` |
| Attach | `core/network/interceptor/AuthInterceptor.kt` | adds `Authorization: Bearer <access>` to every non-`/auth/` request |
| Refresh | `core/network/interceptor/TokenAuthenticator.kt` | on 401, refreshes and retries |
| Repo | `core/data/repository/AuthRepositoryImpl.kt` | OTP request/verify, logout |

**Refresh flow** (`TokenAuthenticator`, an OkHttp `Authenticator`): gives up after 2 attempts;
`synchronized` so concurrent 401s don't double-refresh (if the stored token already changed, it
just retries with the fresh one); otherwise calls `AuthApi.refresh` (injected as
`dagger.Lazy<AuthApi>` to break the Retrofit⇄Authenticator cycle), then `updateAccessToken` and
retries — or `clear()`s the session on failure, forcing re-login.

**Session state** — `ObserveSessionUseCase` combines `authRepository.isLoggedIn` and
`userRepository.observeMe()` into `SessionState { LoggedOut, NeedsProfile(me), Ready(me),
Loading }`, which the app uses to choose the auth vs main graph.

> **Note:** tokens are stored in **plain** Preferences DataStore (no encryption) — acceptable
> for this stage, worth hardening (e.g. `EncryptedFile`/Keystore) before a production release.

## Repositories (`:core:data`)

Nine impls in `repository/`, bound interface→impl in `di/DataModule.kt` (`@Binds @Singleton`).
Pattern: `@Singleton`, inject API(s) + optional DAO + `@Dispatcher(NearAidDispatcher.IO)`, and
every method is `withContext(ioDispatcher) { safeApiCall { ... } }` returning `DataResult`.
(`PreferencesRepositoryImpl` is the exception — a pure DataStore passthrough.)

**Mapping** (`mapper/`): `Mappers.kt` (`XDto.toDomain()` + string→enum helpers, incl. the
delivered-state rule `claimStatusOf(status, deliveredAt)`), `Wire.kt` (domain enum → wire
string, `.wire()`), `CacheMappers.kt` (domain ↔ Room entity).

## Realtime chat (`ChatSocket`)

`core/network/socket/ChatSocket.kt` opens an OkHttp WebSocket via `callbackFlow`:
`observe(threadId): Flow<ChatMessage>` connects to `"$wsUrl?token=$access"`, sends a
`{"event":"subscribe","thread_id":…}` frame on open, and emits frames where
`event == "message.new"` for that thread. **No reconnection** — the transport is best-effort;
REST (`getMessages`) is the source of truth, so a dropped socket degrades gracefully.
`ChatRepositoryImpl.observeThread` exposes it; `getConversations`/`getMessages`/`sendMessage`/
`markRead` cover the REST side.

## Local storage

**DataStore (`:core:datastore`)** — Preferences DataStore file `nearaid_prefs`.
`AuthPreferencesDataSource` (tokens) and `UserPreferencesDataSource` (`language`,
`search_radius_km` default 5.0) — the latter surfaced through `PreferencesRepositoryImpl`.

**Room (`:core:database`)** — `NearAidDatabase` (`nearaid.db`, v1,
`fallbackToDestructiveMigration()`) with two cache tables:

| Entity | Table | Cached for |
|---|---|---|
| `CachedListingEntity` | `cached_listings` (keyed by feed type) | offline discovery feed |
| `CachedConversationEntity` | `cached_conversations` | offline conversation list |

Caches are **write-on-first-page** and read only as a fallback when the network call fails
(e.g. `ListingRepositoryImpl.getNearby` returns cached rows on failure when `cursor == null`).
Messages, claims, profile and notifications are **not** cached. The DB is disposable (single
version, destructive migration).

## Push (FCM)

`app/fcm/NearAidMessagingService.kt` (`@AndroidEntryPoint FirebaseMessagingService`) —
implemented, not stubbed. `onNewToken` → `RegisterDeviceUseCase` (`POST me/devices` with
`DeviceBody`); `onMessageReceived` builds a `MainActivity` PendingIntent carrying
`EXTRA_DEEPLINK` and posts a notification on the `nearaid_alerts` channel created by
`NearAidApplication`.
