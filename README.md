# NearAid — Android App

**NearAid** (পাশের মানুষ — *"the person beside you"*) is a hyperlocal **mutual‑aid** Android
client. It connects people who need everyday help — food, clothes, medicine, household
goods, shelter — with nearby neighbours willing to give. It is a two‑sided board:

- A **Seeker** posts a **request** ("I need…"); nearby **Helpers** claim it, coordinate in‑app, and hand off in person.
- A **Giver** posts an **offer** ("I have surplus to give"); nearby **Recipients** request it and arrange pickup.

The app handles **no money** — it is a discovery + coordination layer; the exchange happens
offline at a mutually agreed public place. Discovery is **list‑based** with privacy‑fuzzed
location and banded distance (a dedicated map view is deferred to a later release).

> Built with **MVI + Clean Architecture**, a **multi‑module** Gradle setup, Jetpack Compose,
> type‑safe Navigation, Hilt, Room, DataStore, Retrofit, Coroutines/Flow, an OkHttp
> WebSocket for realtime chat, and Firebase Cloud Messaging for push.

UI follows `near_aid_documents/nearaid_ui.html`; behaviour and data follow the
**NearAid Technical Documentation v1.1** and the OpenAPI spec (`NearAid API.yaml`).

---

## Table of contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Module graph](#module-graph)
4. [Tech stack](#tech-stack)
5. [Project structure](#project-structure)
6. [Data flow (end to end)](#data-flow-end-to-end)
7. [Networking, realtime & auth](#networking-realtime--auth)
8. [Navigation](#navigation)
9. [Build & run](#build--run)
10. [Configuration](#configuration)
11. [Conventions](#conventions)
12. [Testing](#testing)
13. [Implementation status](#implementation-status)
14. [Contributing](#contributing)
15. [License](#license)

---

## Features

- **Phone‑first auth** — OTP sign‑in (E.164), profile setup, optional ID‑verification badge.
- **Two‑sided discovery** — a Needs/Offers toggle, category & urgency filters, distance‑sorted
  list of nearby listings (privacy‑fuzzed location, banded distance).
- **Post** — a center `+` chooser ("I need help" vs "I have something to give") → create a
  request (with urgency) or an offer (with an availability window).
- **Claim & fulfil** — claim an open listing → a private chat opens → fulfilling party marks
  *delivered* → receiving party *confirms* → both parties rate each other.
- **Messages** — all conversation threads in one tab; realtime 1:1 chat over WebSocket with a
  safety bar suggesting public meetup points.
- **Profile & trust** — trust score, ratings, verification, language (Bangla/English) & settings.
- **Safety** — report listings/users, block, and a moderation‑friendly backend contract.
- **Push** — FCM notifications for nearby urgent needs/offers, claims, messages and ratings.

---

## Architecture

The app combines **Clean Architecture** (layering + dependency rule) with **MVI**
(unidirectional UI state) in the presentation layer.

```
            ┌──────────────────────────────────────────────────────┐
            │                     PRESENTATION                      │
            │   Compose Screen ──Intent──▶ ViewModel (MVI)          │
            │        ▲                         │                    │
            │   State│                    UseCase calls             │
            │        └────────StateFlow───────┘   Effect (one-shot) │
            └───────────────────────┬──────────────────────────────┘
                                    │ depends on
            ┌───────────────────────▼──────────────────────────────┐
            │                       DOMAIN                          │
            │   UseCases ──▶ Repository interfaces ──▶ Models        │
            │   (pure Kotlin; no Android/IO framework types)        │
            └───────────────────────┬──────────────────────────────┘
                                    │ implemented by
            ┌───────────────────────▼──────────────────────────────┐
            │                        DATA                           │
            │   RepositoryImpl ──▶ Mappers ──▶ Network / Room /      │
            │                                  DataStore             │
            └───────────────────────────────────────────────────────┘
```

**Dependency rule:** dependencies point inward. `feature → domain`, `data → domain`, and
nothing depends on `feature`/`data` except the `:app` module, which wires everything together
via Hilt. **ViewModels depend on use cases, never on repositories directly.**

### MVI contract

Every screen defines three types (in a `*Contract.kt` file):

| Type       | Direction       | Purpose                                                    |
|------------|-----------------|------------------------------------------------------------|
| `UiState`  | ViewModel → UI  | Immutable snapshot the screen renders (a `StateFlow`).     |
| `UiIntent` | UI → ViewModel  | User actions / inputs, funneled through `onIntent(...)`.   |
| `UiEffect` | ViewModel → UI  | One‑shot events: navigation, snackbars (a `Channel` flow). |

`MviViewModel` (in `:core:common`) is the base class: it owns the `StateFlow`, exposes
`onIntent(...)`, and offers `setState { copy(...) }` / `sendEffect(...)`. Screens collect the
one‑shot `effect` flow lifecycle‑safely via `CollectEffect`. See `feature/auth/phone/` for the
canonical example.

---

## Module graph

```
                          ┌─────────┐
                          │  :app   │  (Hilt host, NavHost, bottom nav, FCM)
                          └────┬────┘
              ┌────────────────┼─────────────────────────┐
              ▼                ▼                          ▼
        :feature:auth   :feature:discovery   …   :feature:profile
              │                │                          │
              └───────┬────────┴──────────────────────────┘
                      ▼ (every feature depends on)
   :core:domain ─ :core:model ─ :core:common ─ :core:designsystem ─ :core:navigation
                      ▲
                      │ implemented by
                 :core:data
                      │
        ┌─────────────┼──────────────┐
        ▼             ▼              ▼
  :core:network  :core:database  :core:datastore
```

- **Features never depend on other features.** They share only `:core:*` modules and navigate
  to each other through the central type‑safe routes in `:core:navigation`.
- **`:core:data`** is the only module that knows `:core:network`, `:core:database` and
  `:core:datastore` at once; it stitches them into repositories.
- **`:app`** is the composition root: it depends on every feature + core module so Hilt can
  assemble the graph, owns the navigation host, and provides `@BaseUrl`/`@WsUrl` from `BuildConfig`.

---

## Tech stack

| Concern              | Choice                                                            |
|----------------------|-------------------------------------------------------------------|
| Language             | Kotlin                                                            |
| UI                   | Jetpack Compose + Material 3                                       |
| Navigation           | Navigation‑Compose **type‑safe** routes (`@Serializable`)         |
| DI                   | Hilt                                                              |
| Async                | Coroutines + Flow                                                  |
| Networking           | Retrofit + OkHttp + kotlinx.serialization                         |
| Realtime             | OkHttp **WebSocket** (1:1 chat, §10)                              |
| Push                 | Firebase Cloud Messaging                                          |
| Local DB             | Room (offline cache for feed + conversations)                     |
| Key‑value storage    | DataStore Preferences (JWT tokens + language/radius)              |
| Images               | Coil                                                              |
| Build                | Gradle Kotlin DSL + version catalog + **convention plugins**      |
| Testing              | JUnit4, MockK, Turbine, kotlinx‑coroutines‑test                   |

Versions are centralized in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## Project structure

```
near_aid_app/
├── build-logic/                      # Convention plugins (shared Gradle config)
│   └── convention/src/main/kotlin/
│       ├── NearAidBuildConfig.kt          # SDK/Java targets in one place (35 / 17)
│       ├── KotlinAndroid.kt / AndroidCompose.kt
│       ├── AndroidApplication[Compose]ConventionPlugin.kt
│       ├── AndroidLibrary[Compose]ConventionPlugin.kt
│       ├── AndroidFeatureConventionPlugin.kt   # library+compose+hilt+core deps wired
│       ├── AndroidHiltConventionPlugin.kt
│       ├── AndroidRoomConventionPlugin.kt
│       └── JvmLibraryConventionPlugin.kt
│
├── app/                              # Application module (composition root)
│   └── src/main/java/com/nearaid/
│       ├── NearAidApplication.kt          # @HiltAndroidApp + notification channel
│       ├── MainActivity.kt                # splash + start destination from session
│       ├── MainViewModel.kt               # observes login state (use case)
│       ├── di/AppModule.kt                # provides @BaseUrl / @WsUrl from BuildConfig
│       ├── fcm/NearAidMessagingService.kt # FCM receive + device-token registration
│       └── navigation/
│           ├── NearAidNavHost.kt          # Auth graph ⇄ Main graph
│           ├── MainScreen.kt              # bottom-nav shell + nested NavHost
│           └── TopLevelDestination.kt     # Home / Activity / Messages / Profile (+ Post FAB)
│
├── core/
│   ├── model/         # Pure-Kotlin domain models (Listing, Claim, Conversation, Me, …)
│   ├── common/        # MviViewModel + contract, DataResult/AppError, @Dispatcher, TimeFormat
│   ├── designsystem/  # Marigold + Tulsi-teal theme, components, CollectEffect
│   ├── navigation/    # Type-safe @Serializable route definitions
│   ├── datastore/     # AuthPreferencesDataSource (JWT) + UserPreferencesDataSource (prefs)
│   ├── network/       # Retrofit APIs, DTOs, AuthInterceptor, TokenAuthenticator,
│   │                  #   safeApiCall (error envelope), ChatSocket (WebSocket), NetworkModule
│   ├── database/      # Room database, cache entities, DAOs
│   ├── domain/        # Repository interfaces (one per file) + UseCases (one per action)
│   └── data/          # Repository implementations + DTO/Entity↔Model mappers + DataModule
│
└── feature/
    ├── auth/          # Splash · Welcome · Phone · OTP · Profile setup
    ├── discovery/     # Home (Needs/Offers feed) · Listing detail (claim/report) · Notifications
    ├── post/          # Post chooser · Create request/offer
    ├── activity/      # My claims (Helping) + my posts, with lifecycle actions
    ├── messages/      # Conversation list · realtime Chat
    └── profile/       # Profile · Public profile · Verification · Settings
```

Each **feature module** follows the same internal shape:

```
feature/<name>/src/main/kotlin/com/nearaid/feature/<name>/
├── <screen>/
│   ├── <Screen>Contract.kt     # State / Intent / Effect
│   ├── <Screen>ViewModel.kt    # extends MviViewModel, injects UseCases
│   └── <Screen>Screen.kt       # Composable (collects state + effect)
└── navigation/
    └── <Name>Navigation.kt     # NavGraphBuilder extension registering the destinations
```

---

## Data flow (end to end)

**OTP sign‑in**, traced through the layers:

1. **UI** — `PhoneScreen` sends `PhoneIntent.Submit`; after the code is sent, `OtpScreen`
   sends `OtpIntent.Verify`.
2. **ViewModel** — `OtpViewModel` calls `VerifyOtpUseCase(requestId, code)`.
3. **UseCase** (`:core:domain`) — delegates to `AuthRepository.verifyOtp(...)`.
4. **Repository** (`:core:data`) — `AuthRepositoryImpl` calls `AuthApi.verifyOtp(...)` via
   `safeApiCall { }`, then persists the JWT pair + user id through `AuthPreferencesDataSource`.
5. **Result** — a `DataResult<AuthSession>` flows back up. On success the ViewModel emits
   `OtpEffect.Verified(isNewUser)`; on failure it maps `AppError` to a message in state.
6. **UI** — `CollectEffect` observes the effect and navigates (profile setup for new users,
   otherwise the main graph).

The **nearby feed** is cache‑backed: `ListingRepositoryImpl` returns the network page on
success (writing it to Room), and falls back to the Room cache when the network is
unavailable — so the last feed and conversation list survive offline (NFR §5).

---

## Networking, realtime & auth

- **Base URL / WS URL** are build‑type specific (`BuildConfig.BASE_URL` / `WS_URL`) and injected
  into the network layer via the `@BaseUrl` / `@WsUrl` qualifiers — debug → local backend,
  release → production.
- **`AuthInterceptor`** attaches `Authorization: Bearer <access>` to every non‑`/auth/` request.
- **`TokenAuthenticator`** transparently refreshes the access token on a `401` via
  `POST /auth/refresh`, retries the request once, and clears the session if refresh fails.
- **`safeApiCall`** normalizes transport failures + the API's `{"error":{code,message,details}}`
  envelope (§9.1) into a domain `AppError`, so the UI never sees Retrofit/OkHttp types.
- **`ChatSocket`** opens `wss://…/ws?token=…`, subscribes to a claim thread, and emits incoming
  `message.new` events as domain `ChatMessage`s; chat history is fetched over REST.
- **Privacy:** exact coordinates are never requested for the public feed — only a fuzzed point
  and a banded distance (§13.1).

### API surface mapped

| Area          | Endpoints                                                                       |
|---------------|---------------------------------------------------------------------------------|
| Auth          | `/auth/otp/request`, `/auth/otp/verify`, `/auth/refresh`, `/auth/logout`        |
| Users / me    | `/me`, `/me/devices`, `/me/verification`, `/users/{id}`, `/users/{id}/ratings`  |
| Categories    | `/categories`                                                                   |
| Listings      | `/listings`, `/listings/nearby`, `/listings/{id}`, `…/claim`, `…/cancel`, `/me/listings` |
| Claims        | `/claims/{id}/withdraw\|deliver\|confirm\|rating`, `/me/claims`                  |
| Chat          | `/me/conversations`, `/claims/{id}/messages`, `…/messages/read`, WS (§10)        |
| Safety        | `/reports`, `/blocks`, `/me/blocks`                                              |
| Notifications | `/me/notifications`, `/me/notifications/read`                                    |

---

## Navigation

Type‑safe Navigation‑Compose. Destinations are `@Serializable` types in `:core:navigation`:

```kotlin
@Serializable data object HomeRoute
@Serializable data class  ListingDetailRoute(val listingId: String)
@Serializable data class  ChatRoute(val claimId: String, val threadId: String, val title: String)
```

- The root `NearAidNavHost` switches between `AuthGraph` and `MainGraph`. Authenticating pops
  the auth graph; logging out pops the main graph — back navigation never crosses the auth boundary.
- `MainScreen` hosts the five‑slot bottom bar (**Home · Activity · Post (+) · Messages · Profile**,
  the center Post being a FAB that opens the request‑vs‑offer chooser) over a nested `NavHost`.
- Each feature exposes a `NavGraphBuilder.<name>Graph(navController, …)` extension and registers
  only its own destinations, so **no feature references another feature**.

---

## Build & run

**Requirements:** Android Studio (Ladybug+), **JDK 17**, Android SDK 35.

```bash
# from near_aid_app/
./gradlew assembleDebug          # build the debug APK
./gradlew installDebug           # install on a connected device/emulator
./gradlew test                   # unit tests (all modules)
./gradlew :app:lint              # Android lint
```

> **Toolchain note.** The build uses **AGP 8.7.3 / Kotlin 2.0.21 / Compose BOM 2024.12 / KSP
> 2.0.21‑1.0.27** — a known‑good, mutually‑compatible matrix (Hilt's Gradle plugin does not yet
> support AGP 9). If only an older JDK is on your `PATH`, point Gradle at the Android Studio
> bundled JBR:
> ```bash
> export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
> ```

---

## Configuration

- **Backend URL.** Debug builds target a local backend at `http://10.0.2.2:8000/v1/`
  (`10.0.2.2` = the host machine from the Android emulator); release targets the production
  domain. Change these in [`app/build.gradle.kts`](app/build.gradle.kts) (`BASE_URL` / `WS_URL`
  build‑config fields). A **debug‑only** network‑security config permits cleartext to
  `10.0.2.2` / `localhost` so you can run against a plain‑HTTP dev server.
- **Push (FCM).** The `firebase-messaging` dependency and the messaging service are wired, but a
  `google-services.json` + the `google-services` Gradle plugin are intentionally **not** checked
  in — add them to enable real push delivery.
- **Deferred in v1 (by design):** dedicated map view, real device GPS (the feed currently uses a
  fixed Dhaka coordinate, marked with `// TODO`), and the custom Bricolage / Plus‑Jakarta /
  Hind‑Siliguri fonts (the type scale is in place; assets aren't bundled yet).

---

## Conventions

- **Convention plugins** (`build-logic/`) keep every module's `build.gradle.kts` to a few lines.
  A feature module is just:
  ```kotlin
  plugins { alias(libs.plugins.nearaid.android.feature) }
  android { namespace = "com.nearaid.feature.<name>" }
  ```
- **One use case = one action.** ViewModels inject use cases (e.g. `GetNearbyListingsUseCase`),
  never repositories. Repository interfaces live one‑per‑file in `:core:domain/repository`.
- **Mappers** live in `:core:data`; DTOs (`:core:network`) and Room entities (`:core:database`)
  never leak past the data layer — the rest of the app speaks `:core:model` only.
- **Errors are values** (`DataResult` / `AppError`), not exceptions, above the data layer.

---

## Testing

Fast **JVM unit tests** (no device/emulator) cover the presentation and non‑UI layers:
every ViewModel and the pure logic in `:core`. They run as `testDebugUnitTest` — ~150 tests
across the modules below.

- **Frameworks:** JUnit4, **MockK** (use‑case doubles), **Turbine** (effect/flow assertions),
  **kotlinx‑coroutines‑test**. These come wired to every module through the convention plugins,
  so no per‑module test setup is needed.
- **ViewModel pattern:** a `MainDispatcherRule` swaps `Dispatchers.Main` for an
  `UnconfinedTestDispatcher`, use cases are mocked, the VM is driven through `onIntent(...)`, and
  assertions are made on `state.value` and the one‑shot `effect` flow (via Turbine). Both the
  success and failure (`AppError`) branches of each intent are exercised, plus MVI reducers,
  `init{}` loads, validation/submit gating and navigation effects.

| Module            | What's covered                                                                 |
|-------------------|--------------------------------------------------------------------------------|
| `:app`            | `MainViewModel` (login‑state → start destination), `TopLevelDestination` tabs  |
| `:core:common`    | `DataResult` (`map`/`onSuccess`/`onFailure`/`getOrNull`), `TimeFormat` (ISO parse + relative time) |
| `:core:domain`    | `PhoneNumber` (Bangladesh → E.164 normalization & display formatting)          |
| `:core:network`   | `safeApiCall` + `HttpException` → `AppError` mapping (status codes + error envelope) |
| `:feature:auth`   | Phone, OTP and profile‑setup ViewModels (validation, send/verify, navigation)  |
| `:feature:discovery` | Home feed, listing detail (claim/report/block) and notifications ViewModels  |
| `:feature:post`   | Create‑listing ViewModel (field reducers, `canSubmit` gating, request vs offer) |
| `:feature:activity` | Activity ViewModel (claims/listings load, sorting, deliver/confirm/withdraw)  |
| `:feature:messages` | Conversations + realtime Chat ViewModels (history, streamed messages, send)   |
| `:feature:profile` | Profile, public profile, settings and verification ViewModels                 |

```bash
./gradlew test                                    # all unit tests, every module
./gradlew :feature:discovery:testDebugUnitTest    # a single module
```

> UI (Compose), DI wiring, Room DAOs and repository implementations are not yet unit‑tested —
> those need instrumented / Robolectric setup, which is not configured in v1.

---

## Implementation status

| Area                       | State          | Notes                                                       |
|----------------------------|----------------|-------------------------------------------------------------|
| Auth (OTP + profile setup) | ✅ Implemented  | JWT persisted; transparent token refresh.                   |
| Discovery (feed + detail)  | ✅ Implemented  | Needs/Offers toggle, filters, claim, report/block, cache.   |
| Post (request / offer)     | ✅ Implemented  | Category picker, urgency vs availability window.            |
| Activity                   | ✅ Implemented  | Claims (Helping) + my posts; deliver/confirm/withdraw.      |
| Messages + Chat            | ✅ Implemented  | Conversation list + realtime WebSocket chat.                |
| Profile / Trust / Safety   | ✅ Implemented  | Trust score, ratings, verification, language, settings.     |
| Maps (fuzzed pins)         | ⏳ Deferred     | List‑based discovery in v1 (Tech Doc §2).                   |
| Device GPS                 | 🚧 Stub        | Fixed Dhaka location placeholder (`// TODO`).               |
| Push delivery (FCM)        | 🚧 Needs config| Code wired; add `google-services.json`.                     |

The client is feature‑complete against the v1 contract and runs against any backend that
implements the documented API; the items above are the only intentional gaps.

---

## Contributing

Contributions are welcome. To add or change a feature:

1. Add the API in `:core:network` (DTOs + a Retrofit service), the repository in `:core:data`,
   and the interface + use case(s) in `:core:domain`.
2. Build the screen in its feature module as `Contract / ViewModel / Screen` + a navigation
   graph extension, following the patterns above.
3. Keep the dependency rule intact (features depend only on `:core:*`; never on each other).
4. Run `./gradlew assembleDebug test :app:lint` before opening a PR. Match the existing Kotlin
   style and keep modules' `build.gradle.kts` on the convention plugins.

Please open an issue to discuss larger changes first.

---

## License

Released under the **MIT License** — see [`LICENSE`](LICENSE). © NearAid contributors.
