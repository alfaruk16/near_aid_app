# NearAid — Kotlin Multiplatform App (Android + iOS)

[![CI](https://github.com/alfaruk16/near_aid_app/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/alfaruk16/near_aid_app/actions/workflows/ci.yml)
[![Logic coverage](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/alfaruk16/near_aid_app/main/docs/badges/coverage.json)](NearAid_KMP_Report.md#5-metric-evidence)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/multiplatform.html)

> **Coverage badge** shows Kover line coverage of hand‑written logic (ViewModels, repositories,
> use cases, mappers, utilities); UI and generated code are excluded — see
> [the evidence report](NearAid_KMP_Report.md#5-metric-evidence) for the basis and the
> whole‑repository figure. It is regenerated from the merged Kover report and committed by CI
> on every push to `main`.

**NearAid** (পাশের মানুষ — *"the person beside you"*) is a hyperlocal **mutual‑aid**
client. It connects people who need everyday help — food, clothes, medicine, household
goods, shelter — with nearby neighbours willing to give. It is a two‑sided board:

- A **Seeker** posts a **request** ("I need…"); nearby **Helpers** claim it, coordinate in‑app, and hand off in person.
- A **Giver** posts an **offer** ("I have surplus to give"); nearby **Recipients** request it and arrange pickup.

The app handles **no money** — it is a discovery + coordination layer; the exchange happens
offline at a mutually agreed public place. Discovery is **list‑based** with privacy‑fuzzed
location and banded distance (a dedicated map view is deferred to a later release).

> **One shared Kotlin codebase runs on Android and iOS.** Built with **Kotlin Multiplatform**
> + **Compose Multiplatform** — logic *and* UI are shared: **MVI + Clean Architecture**, a
> **multi‑module** Gradle setup with **convention plugins**, Koin DI, Ktor client + WebSocket,
> Room‑KMP, DataStore + platform‑secure token storage, kotlinx.serialization,
> Coroutines/Flow, Coil 3, and SKIE for ergonomic Swift interop. Each platform is a thin host
> (an Android `Activity`, an iOS `UIViewController`).

UI follows `near_aid_documents/nearaid_ui.html`; behaviour and data follow the
**NearAid Technical Documentation v1.1** and the OpenAPI spec (`NearAid API.yaml`). The
Android→KMP migration is documented phase‑by‑phase under [`docs/`](docs) (start with the
[migration roadmap](docs/KMP_MIGRATION_ROADMAP.md)).

---

## Table of contents

1. [Features](#features)
2. [Multiplatform architecture](#multiplatform-architecture)
3. [Module graph](#module-graph)
4. [Tech stack](#tech-stack)
5. [Project structure](#project-structure)
6. [Data flow (end to end)](#data-flow-end-to-end)
7. [Networking, realtime & auth](#networking-realtime--auth)
8. [Dependency injection](#dependency-injection)
9. [Navigation](#navigation)
10. [Accessibility, theming & localization](#accessibility-theming--localization)
11. [Platform edges (`expect`/`actual`)](#platform-edges-expectactual)
12. [Build & run](#build--run)
13. [Configuration](#configuration)
14. [Conventions](#conventions)
15. [Testing](#testing)
16. [Implementation status](#implementation-status)
17. [Contributing](#contributing)
18. [License](#license)

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
- **Push** — FCM notifications for nearby urgent needs/offers, claims, messages and ratings
  (Android; wired, pending Firebase config — see *Implementation status*).
- **Runs on Android and iOS from one codebase** — the same ViewModels, repositories, network,
  database *and* Compose UI render on both platforms; iOS reaches them via a `Shared` framework.
- **Accessible by design** — TalkBack/VoiceOver labels, roles & headings on every interactive
  element, 48 dp touch targets, live‑region status announcements, and a shared accessibility
  contract tested on both platforms.
- **Light & dark theme** — a semantic color system that follows the system setting, with
  contrast‑checked palettes for both modes.
- **Localization‑ready** — all user‑facing copy lives in Compose Multiplatform string resources
  (incl. Bangla), ready for `values-<lang>` translations.

---

## Multiplatform architecture

The app combines **Clean Architecture** (layering + dependency rule) with **MVI**
(unidirectional UI state), and shares nearly everything through Kotlin Multiplatform source sets.

```
            ┌──────────────────────────────────────────────────────┐
            │              PRESENTATION  (commonMain)               │
            │   Compose UI ──Intent──▶ ViewModel (MVI)              │
            │        ▲                         │                    │
            │   State│                    UseCase calls             │
            │        └────────StateFlow───────┘   Effect (one-shot) │
            └───────────────────────┬──────────────────────────────┘
                                    │ depends on
            ┌───────────────────────▼──────────────────────────────┐
            │                 DOMAIN  (commonMain)                  │
            │   UseCases ──▶ Repository interfaces ──▶ Models        │
            │   (pure Kotlin; no platform/IO framework types)       │
            └───────────────────────┬──────────────────────────────┘
                                    │ implemented by
            ┌───────────────────────▼──────────────────────────────┐
            │                  DATA  (commonMain)                   │
            │   RepositoryImpl ──▶ Mappers ──▶ Ktor / Room-KMP /     │
            │                                  DataStore + secure    │
            └───────────────────────┬──────────────────────────────┘
                                    │ expect / actual
            ┌───────────────────────▼──────────────────────────────┐
            │   PLATFORM  (androidMain / iosMain)                   │
            │   OkHttp·Darwin engine · SQLite driver · Keystore·     │
            │   Keychain · image picker · Android host / iOS host   │
            └───────────────────────────────────────────────────────┘
```

- **Almost all code lives in `commonMain`.** Only genuine platform edges — HTTP engine, SQLite
  driver, secure token store, image picker, the app hosts — are `expect`/`actual` split between
  `androidMain` and `iosMain` (see [Platform edges](#platform-edges-expectactual)).
- **Dependency rule:** dependencies point inward. `feature → domain`, `data → domain`, and
  nothing depends on `feature`/`data` except the `:shared`/`:app` hosts, which wire everything
  together via **Koin**. **ViewModels depend on use cases, never on repositories directly.**

### MVI contract

Every screen defines three types (in a `*Contract.kt` file, in `commonMain`):

| Type       | Direction       | Purpose                                                    |
|------------|-----------------|------------------------------------------------------------|
| `UiState`  | ViewModel → UI  | Immutable snapshot the screen renders (a `StateFlow`).     |
| `UiIntent` | UI → ViewModel  | User actions / inputs, funneled through `onIntent(...)`.   |
| `UiEffect` | ViewModel → UI  | One‑shot events: navigation, snackbars (a `Channel` flow). |

`MviViewModel` (in `:core:common`) is the base class, built on the **KMP** `androidx.lifecycle`
ViewModel: it owns the `StateFlow`, exposes `onIntent(...)`, and offers `setState { copy(...) }` /
`sendEffect(...)`. Compose screens collect the one‑shot `effect` flow lifecycle‑safely via
`CollectEffect`; on iOS, **SKIE** surfaces the same `StateFlow` to Swift as an `AsyncSequence`.
See `feature/auth/phone/` for the canonical example.

---

## Module graph

Every module below is a **Kotlin Multiplatform** module (`commonMain` + `androidMain`/`iosMain`),
except `:app` (the Android host). The `:shared` umbrella packages the shared modules into the
`Shared` framework that Xcode links.

```
        ┌──────────────┐                 ┌──────────────────────────┐
        │    :app      │  Android host   │   iosApp (Xcode/SwiftUI) │
        │ (Activity)   │                 │   ContentView → Compose  │
        └──────┬───────┘                 └───────────┬──────────────┘
               │                                     │ links
               └──────────────┬──────────────────────┘
                              ▼
                        ┌───────────┐   App() root: NavHost + theme + splash,
                        │  :shared  │   MainScreen (bottom nav), Koin bootstrap,
                        └─────┬─────┘   Shared iOS framework (api-exports below)
              ┌───────────────┼─────────────────────────┐
              ▼               ▼                          ▼
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
- **`:shared`** is the composition root of the shared UI: it hosts `App()` (the root NavHost,
  theme, and Koin bootstrap `doInitKoin(...)`), `api`‑exports the modules Swift must see, and
  produces the `Shared` iOS framework via SKIE.
- **`:app`** is the thin Android host: `MainActivity` just calls `setContent { KoinAndroidContext { App() } }`;
  it also owns FCM and provides the backend URLs from `BuildConfig`.

---

## Tech stack

| Concern              | Choice                                                            |
|----------------------|-------------------------------------------------------------------|
| Language             | Kotlin **Multiplatform** (2.1.20)                                 |
| UI                   | **Compose Multiplatform** (1.7.3) + Material 3 — shared Android/iOS |
| Navigation           | JetBrains **Navigation‑Compose** (CMP), type‑safe `@Serializable` routes |
| DI                   | **Koin** (multiplatform)                                         |
| Async                | Coroutines + Flow                                                  |
| Swift interop        | **SKIE** — `StateFlow` → `AsyncSequence`, `suspend` → `async`     |
| Networking           | **Ktor Client** (OkHttp / Darwin engines) + kotlinx.serialization |
| Realtime             | **Ktor WebSockets** (1:1 chat, §10)                              |
| Push                 | Firebase Cloud Messaging (Android; iOS APNs deferred)             |
| Local DB             | **Room‑KMP** (bundled SQLite driver) — offline cache             |
| Key‑value storage    | **DataStore‑KMP** (language/radius) + platform‑secure JWT store   |
| Token storage        | Keystore/EncryptedSharedPreferences (Android) · Keychain (iOS)    |
| Images               | **Coil 3** (`AsyncImage` + Ktor network fetcher)                  |
| Theming              | Semantic color system (`NearAidTheme.colors`) + Material 3 light/dark |
| Localization         | Compose Multiplatform resources (`composeResources`, generated `Res`) |
| Accessibility        | Compose semantics (roles, headings, live regions) + shared a11y contract |
| Build                | Gradle Kotlin DSL + version catalog + **KMP/CMP convention plugins** |
| Android host         | `:app` (Hilt not used — Koin), `MainActivity` + FCM              |
| iOS host             | `iosApp` (SwiftUI shell) linking the `Shared` framework           |
| Testing              | JUnit4, MockK, Turbine, kotlinx‑coroutines‑test, **Robolectric**; `commonTest` on JVM + iOS native |

Versions are centralized in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## Project structure

```
near_aid_app/
├── build-logic/                      # Convention plugins (shared Gradle config)
│   └── convention/src/main/kotlin/
│       ├── NearAidBuildConfig.kt          # SDK/Java targets in one place (35 / 17)
│       ├── KotlinAndroid.kt / AndroidCompose.kt
│       ├── AndroidApplication[Compose]ConventionPlugin.kt   # the :app host
│       ├── AndroidLibrary[Compose]ConventionPlugin.kt
│       ├── KmpLibraryConventionPlugin.kt        # nearaid.kmp.library  — KMP core module
│       ├── KmpFeatureConventionPlugin.kt        # nearaid.kmp.feature  — KMP (logic-only) feature
│       ├── CmpLibraryConventionPlugin.kt        # nearaid.cmp.library  — KMP + Compose Multiplatform
│       ├── AndroidFeatureConventionPlugin.kt
│       └── JvmLibraryConventionPlugin.kt
│
├── shared/                           # iOS umbrella + shared app root (CMP module)
│   └── src/
│       ├── commonMain/kotlin/com/nearaid/shared/
│       │   ├── App.kt                     # root NavHost + theme + splash + Coil singleton
│       │   ├── MainScreen.kt              # bottom-nav shell + nested NavHost
│       │   ├── TopLevelDestination.kt     # Home / Activity / Messages / Profile (+ Post FAB)
│       │   ├── MainViewModel.kt           # observes login state → start destination
│       │   ├── Koin.kt                    # doInitKoin(baseUrl, wsUrl) — iOS Koin bootstrap
│       │   └── SharedViewModels.kt        # Koin-resolved ViewModel factories for Swift
│       └── iosMain/kotlin/.../MainViewController.kt   # ComposeUIViewController { App() }
│
├── iosApp/                           # Xcode project (SwiftUI shell)
│   └── iosApp/
│       ├── iOSApp.swift                   # calls KoinKt.doInitKoin(...) at launch
│       └── ContentView.swift              # UIViewControllerRepresentable → MainViewController()
│
├── app/                              # Android host module
│   └── src/main/java/com/nearaid/
│       ├── NearAidApplication.kt          # startKoin + notification channel
│       ├── MainActivity.kt                # setContent { KoinAndroidContext { App() } }
│       ├── di/AppModule.kt                # provides NetworkConfig (BASE_URL / WS_URL) from BuildConfig
│       └── fcm/NearAidMessagingService.kt # FCM receive + device-token registration
│
├── core/                             # all KMP modules (commonMain + platform source sets)
│   ├── model/         # Pure-Kotlin domain models (Listing, Claim, Conversation, Me, …)
│   ├── common/        # MviViewModel + contract, DataResult/AppError, @Dispatcher, TimeFormat, formatOneDecimal
│   ├── designsystem/  # (CMP) Marigold + Tulsi-teal theme, components, CollectEffect, A11y contract
│   ├── navigation/    # Type-safe @Serializable route definitions
│   ├── datastore/     # UserPreferencesDataSource (prefs) + SecureTokenStore (expect/actual)
│   ├── network/       # Ktor client + APIs, DTOs, Auth/refresh plugin, safeApiCall, ChatSocket (WS)
│   ├── database/      # Room-KMP database, cache entities, DAOs (bundled SQLite driver)
│   ├── domain/        # Repository interfaces (one per file) + UseCases (one per action)
│   └── data/          # Repository implementations + DTO/Entity↔Model mappers + Koin data module
│
└── feature/                          # KMP + CMP feature modules
    ├── auth/          # Splash · Welcome · Phone · OTP · Profile setup
    ├── discovery/     # Home (Needs/Offers feed) · Listing detail (claim/report) · Notifications
    ├── post/          # Post chooser · Create request/offer
    ├── activity/      # My claims (Helping) + my posts, with lifecycle actions
    ├── messages/      # Conversation list · realtime Chat
    └── profile/       # Profile · Public profile · Verification (image picker) · Settings
```

Each **feature module** follows the same internal shape across source sets:

```
feature/<name>/src/
├── commonMain/kotlin/com/nearaid/feature/<name>/
│   ├── <screen>/
│   │   ├── <Screen>Contract.kt     # State / Intent / Effect        (shared)
│   │   ├── <Screen>ViewModel.kt    # extends MviViewModel, injects UseCases (shared)
│   │   └── <Screen>Screen.kt       # Composable — Compose Multiplatform (shared)
│   ├── navigation/<Name>Navigation.kt   # NavGraphBuilder extension (shared)
│   ├── di/<Name>Module.kt          # Koin module (viewModelOf)      (shared)
│   └── composeResources/values/strings.xml   # localized copy      (shared)
├── androidMain/…      # Android-only actuals (e.g. image picker)
├── iosMain/…          # iOS-only actuals (e.g. PHPickerViewController)
└── androidUnitTest/…  # ViewModel tests (MockK/Turbine are JVM-only)
```

---

## Data flow (end to end)

**OTP sign‑in**, traced through the layers (identical on Android and iOS):

1. **UI** — `PhoneScreen` sends `PhoneIntent.Submit`; after the code is sent, `OtpScreen`
   sends `OtpIntent.Verify`.
2. **ViewModel** — `OtpViewModel` calls `VerifyOtpUseCase(requestId, code)`.
3. **UseCase** (`:core:domain`) — delegates to `AuthRepository.verifyOtp(...)`.
4. **Repository** (`:core:data`) — `AuthRepositoryImpl` calls the Ktor `AuthApi.verifyOtp(...)` via
   `safeApiCall { }`, then persists the JWT pair + user id through `AuthPreferencesDataSource`,
   which writes them to the platform‑secure `SecureTokenStore` (Keystore / Keychain).
5. **Result** — a `DataResult<AuthSession>` flows back up. On success the ViewModel emits
   `OtpEffect.Verified(isNewUser)`; on failure it maps `AppError` to a message in state.
6. **UI** — `CollectEffect` observes the effect and navigates (profile setup for new users,
   otherwise the main graph).

The **nearby feed** is cache‑backed: `ListingRepositoryImpl` returns the network page on
success (writing it to Room‑KMP), and falls back to the Room cache when the network is
unavailable — so the last feed and conversation list survive offline (NFR §5).

---

## Networking, realtime & auth

- **Base URL / WS URL** are supplied as a `NetworkConfig`: Android reads them from
  `BuildConfig.BASE_URL` / `WS_URL`; iOS passes them into `doInitKoin(baseUrl, wsUrl)` at launch.
  Debug → local backend, release → production.
- **Ktor Client** with the platform engine — **OkHttp on Android, Darwin on iOS** (via
  `expect`/`actual`) — and `ContentNegotiation` + kotlinx.serialization for JSON.
- **Bearer auth + refresh** — a Ktor `Auth` provider attaches `Authorization: Bearer <access>`
  and transparently refreshes the access token on a `401` via `POST /auth/refresh`, retrying once
  and clearing the session if refresh fails (the KMP replacement for the old
  `AuthInterceptor`/`TokenAuthenticator`).
- **`safeApiCall`** normalizes transport failures + the API's `{"error":{code,message,details}}`
  envelope (§9.1) into a domain `AppError`, so the UI never sees Ktor/transport types.
- **`ChatSocket`** opens `wss://…/ws?token=…` via the **Ktor WebSockets** plugin, subscribes to a
  claim thread, and emits incoming `message.new` events as domain `ChatMessage`s; chat history is
  fetched over REST.
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
| Safety        | `/reports`, `/blocks`, `/me/blocks`                                             |
| Notifications | `/me/notifications`, `/me/notifications/read`                                   |

---

## Dependency injection

DI is **Koin** (multiplatform — Hilt is Android/JVM‑only). Each module contributes a Koin
`module {}` (repositories in `:core:data`, ViewModels via the `viewModelOf` DSL in each feature).

- **Android** — `NearAidApplication` calls `startKoin { … }` with the `NetworkConfig` from
  `BuildConfig`; `MainActivity` wraps the UI in `KoinAndroidContext`.
- **iOS** — `doInitKoin(baseUrl, wsUrl)` (`shared/Koin.kt`) starts the same `commonMain` modules
  with `NetworkConfig` passed from Swift; the DataStore/Room platform modules resolve their iOS
  `actual`s (Darwin engine, bundled SQLite driver, `NSDocumentDirectory`, Keychain).
- ViewModels are Koin factories; Compose obtains them with `org.koin.compose.viewmodel.koinViewModel`,
  and `SharedViewModels` exposes them to Swift as resolved factories.

---

## Navigation

Type‑safe **JetBrains Navigation‑Compose** (Compose Multiplatform). Destinations are
`@Serializable` types in `:core:navigation`:

```kotlin
@Serializable data object HomeRoute
@Serializable data class  ListingDetailRoute(val listingId: String)
@Serializable data class  ChatRoute(val claimId: String, val threadId: String, val title: String)
```

- `App()` (in `:shared`) hosts the root `NavHost`, switching between `AuthGraph` and `MainGraph`.
  Authenticating pops the auth graph; logging out pops the main graph — back navigation never
  crosses the auth boundary.
- `MainScreen` hosts the five‑slot bottom bar (**Home · Activity · Post (+) · Messages · Profile**,
  the center Post being a FAB that opens the request‑vs‑offer chooser) over a nested `NavHost`.
- Each feature exposes a `NavGraphBuilder.<name>Graph(navController, …)` extension and registers
  only its own destinations, so **no feature references another feature**.

---

## Accessibility, theming & localization

These three concerns are wired into `:core:designsystem` so features get them "for free."

### Accessibility

Reusable semantic modifiers live in
[`core/designsystem/…/component/Accessibility.kt`](core/designsystem/src/commonMain/kotlin/com/nearaid/core/designsystem/component):

- `Modifier.accessibleClickable(onClickLabel, role)` — merges a row into one screen‑reader node,
  sets the role (e.g. `Button`), speaks an action label, and guarantees a **48 dp** touch target.
- `Modifier.headingSemantics()` — marks titles/section labels as headings (TalkBack/VoiceOver rotor).
- `Modifier.politeLiveRegion()` / `Modifier.statusSemantics(text)` — announce loading/error/empty
  states as they appear.

The 48 dp min‑target size and the "every clickable node is labeled" rule are codified in a shared
**`A11y`** contract (designsystem `commonMain`) that both the production modifiers and the tests
reference. Accessibility lint checks (`ContentDescription`, `ClickableViewAccessibility`, `LabelFor`, …)
are promoted to **errors** via a shared root [`lint.xml`](lint.xml).

### Theming (light / dark)

Colors are **semantic**, not hard‑coded. Components read `NearAidTheme.colors.<token>` (e.g.
`ink`, `surface`, `marigold`) rather than raw `Color` values, so the whole design system reacts to
the system light/dark setting. The palette (brand + category + urgency accents) has
contrast‑checked light **and** dark variants; `NearAidTheme(darkTheme = …)` provides both the
Material 3 `ColorScheme` and the custom `NearAidColors` through a `CompositionLocal`. See
[`theme/Color.kt`](core/designsystem/src/commonMain/kotlin/com/nearaid/core/designsystem/theme) and
`theme/Theme.kt`.

### Localization

Every user‑facing string is a **Compose Multiplatform resource** — each module owns its
`src/commonMain/composeResources/values/strings.xml`, and composables use the generated
`Res.string.…` (with positional format args for interpolated text). Bangla copy is already
extracted. To add a language, drop a `composeResources/values-<lang>/strings.xml` into each module.
A few strings that originate in ViewModels / non‑composable helpers are intentionally still inline
(see *Implementation status*).

---

## Platform edges (`expect`/`actual`)

The only code that is not shared is the genuine platform surface, split by source set:

| Concern              | Android `actual`                                   | iOS `actual`                                    |
|----------------------|----------------------------------------------------|-------------------------------------------------|
| HTTP engine          | OkHttp                                              | Darwin                                          |
| SQLite driver        | Room Android / bundled                             | Room native (bundled driver)                    |
| Secure token store   | `EncryptedSharedPreferences` (Keystore AES‑GCM)    | **Keychain** (`kSecClassGenericPassword`)       |
| Preferences path     | `filesDir`                                         | `NSDocumentDirectory`                           |
| Image picker         | `GetContent` + cache‑copy                          | `PHPickerViewController` → `NSTemporaryDirectory` |
| App host             | `MainActivity` (`setContent { App() }`)            | `ContentView` → `MainViewController()`          |
| Push                 | FCM service                                        | APNs + Firebase iOS SDK *(deferred)*            |

`Dispatchers.IO` is provided per platform, and image loading uses Coil 3 with a Ktor network
fetcher over each platform's already‑linked engine.

---

## Build & run

**Requirements:** Android Studio (Ladybug+), **JDK 17**, Android SDK 35, and — for iOS — a Mac
with **Xcode** and the iOS Simulator.

### Android

```bash
# from near_aid_app/
./gradlew assembleDebug          # build the debug APK
./gradlew installDebug           # install on a connected device/emulator
./gradlew test                   # unit tests (all modules)
./gradlew :app:lint              # Android lint
```

### iOS

```bash
# link the shared framework for the simulator (Xcode's build phase also runs this)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# then build/run from Xcode:
open iosApp/iosApp.xcodeproj      # ▶ on an iPhone simulator

# or from the CLI:
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 16'
```

The Xcode "Compile Kotlin Framework" build phase runs
`:shared:embedAndSignAppleFrameworkForXcode`, so building the app compiles the shared Kotlin first.
To sanity‑check that everything compiles for iOS without Xcode:
`./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`.

> **Toolchain note.** The build uses a mutually‑compatible KMP matrix: **Kotlin 2.1.20 / AGP 8.7.3
> / KSP 2.1.x (KSP1 line) / Compose Multiplatform 1.7.3 / Ktor 3.0.3 / Room‑KMP 2.7.1 / Koin 4.0.3 /
> SKIE 0.10.4**. Kotlin is pinned to 2.1.20 because **Room‑KMP 2.7's Native klibs require it**, and
> KSP is pinned to the **KSP1** line (`-1.0.x`) because Room's Native processor fails under KSP2. If
> only an older JDK is on your `PATH`, point Gradle at the Android Studio bundled JBR:
> ```bash
> export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
> ```

---

## Configuration

- **Backend URL.** Debug builds target a local backend at `http://10.0.2.2:8000/v1/`
  (`10.0.2.2` = the host machine from the Android emulator); release targets the production
  domain. Change these in [`app/build.gradle.kts`](app/build.gradle.kts) (`BASE_URL` / `WS_URL`
  build‑config fields). On iOS, the URLs are passed into `doInitKoin(...)` in
  [`iosApp/iosApp/iOSApp.swift`](iosApp/iosApp/iOSApp.swift). A **debug‑only** network‑security
  config permits cleartext to `10.0.2.2` / `localhost` so you can run against a plain‑HTTP dev server.
- **Push (FCM).** The `firebase-messaging` dependency and the messaging service are wired, but a
  `google-services.json` + the `google-services` Gradle plugin are intentionally **not** checked
  in — add them to enable real push delivery. iOS APNs/Firebase is not yet built.
- **Deferred in v1 (by design):** dedicated map view, real device GPS (the feed currently uses a
  fixed Dhaka coordinate, marked with `// TODO`), the custom Bricolage / Plus‑Jakarta /
  Hind‑Siliguri fonts (the type scale is in place; assets aren't bundled yet), iOS push, and
  iOS CI/TestFlight (see *Implementation status*).

---

## Conventions

- **Convention plugins** (`build-logic/`) keep every module's `build.gradle.kts` to a few lines.
  A shared feature module is just:
  ```kotlin
  plugins { alias(libs.plugins.nearaid.cmp.library) }
  android { namespace = "com.nearaid.feature.<name>" }
  ```
  (`nearaid.kmp.library` for a non‑UI core module, `nearaid.cmp.library` for a Compose‑MP feature.)
- **Share by default; split only at the edge.** New code goes in `commonMain`. Reach for
  `expect`/`actual` (`androidMain`/`iosMain`) only for a real platform capability.
- **One use case = one action.** ViewModels inject use cases (e.g. `GetNearbyListingsUseCase`),
  never repositories. Repository interfaces live one‑per‑file in `:core:domain/repository`.
- **Mappers** live in `:core:data`; DTOs (`:core:network`) and Room entities (`:core:database`)
  never leak past the data layer — the rest of the app speaks `:core:model` only.
- **Errors are values** (`DataResult` / `AppError`), not exceptions, above the data layer.

---

## Testing

Fast **JVM unit tests** (no device/emulator) cover the presentation and non‑UI layers:
every ViewModel and the pure logic in `:core`. ViewModel tests run from `androidUnitTest`
(MockK/Turbine are JVM‑only), while pure logic is increasingly tested in `commonTest` so it runs
on **both** the JVM and iOS native.

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
| `:core:designsystem` | Compose **accessibility** tests (Robolectric) + a shared **`A11yContractTest`** running on JVM *and* iOS native |
| `:core:common`    | `DataResult` (`map`/`onSuccess`/`onFailure`/`getOrNull`), `TimeFormat` (ISO parse + relative time) |
| `:core:domain`    | `PhoneNumber` (Bangladesh → E.164 normalization & display formatting)          |
| `:core:network`   | `safeApiCall` + error → `AppError` mapping (status codes + error envelope)      |
| `:feature:auth`   | Phone, OTP and profile‑setup ViewModels (validation, send/verify, navigation)  |
| `:feature:discovery` | Home feed, listing detail (claim/report/block) and notifications ViewModels  |
| `:feature:post`   | Create‑listing ViewModel (field reducers, `canSubmit` gating, request vs offer) |
| `:feature:activity` | Activity ViewModel (claims/listings load, sorting, deliver/confirm/withdraw) |
| `:feature:messages` | Conversations + realtime Chat ViewModels (history, streamed messages, send)   |
| `:feature:profile` | Profile, public profile, settings and verification ViewModels                 |

```bash
./gradlew test                                     # all unit tests, every module
./gradlew :feature:discovery:testDebugUnitTest     # a single module (Android/JVM)
./gradlew :core:designsystem:iosSimulatorArm64Test # commonTest on iOS native (e.g. a11y contract)
```

**Compose accessibility tests** run on the JVM via **Robolectric** (no emulator) in
`:core:designsystem` — `AccessibilityTest` (chip/tab role, selected state, touch target) and
`AccessibilityChecksTest` (scans the whole semantics tree; fails if any clickable node is
unlabeled or under 48 dp). The 48 dp / labeled‑node rules come from the shared `A11y` contract,
which `A11yContractTest` also verifies on iOS native.

> DI wiring, Room DAOs and repository implementations are not yet unit‑tested; full‑screen Compose
> UI tests remain minimal; and iOS rendering‑based a11y (via `runComposeUiTest`) is a follow‑up —
> the a11y *rules* are shared and tested on both platforms, but the rendering harness stays
> Android‑only for now.

---

## Implementation status

| Area                       | State          | Notes                                                       |
|----------------------------|----------------|-------------------------------------------------------------|
| Shared logic (Android+iOS) | ✅ Implemented  | Models → data → ViewModels all in `commonMain` (Koin/Ktor/Room‑KMP). |
| Shared UI (Compose MP)     | ✅ Implemented  | One Compose tree renders on Android and iOS; thin hosts each side. |
| Auth (OTP + profile setup) | ✅ Implemented  | JWT persisted in secure store; transparent token refresh.   |
| Discovery (feed + detail)  | ✅ Implemented  | Needs/Offers toggle, filters, claim, report/block, cache.   |
| Post (request / offer)     | ✅ Implemented  | Category picker, urgency vs availability window.            |
| Activity                   | ✅ Implemented  | Claims (Helping) + my posts; deliver/confirm/withdraw.      |
| Messages + Chat            | ✅ Implemented  | Conversation list + realtime Ktor WebSocket chat.           |
| Profile / Trust / Safety   | ✅ Implemented  | Trust score, ratings, verification, language, settings.     |
| Image picker               | ✅ Implemented  | Android `GetContent` + iOS `PHPickerViewController` actuals. |
| Secure token storage       | ✅ Implemented  | Keystore/EncryptedSharedPreferences (Android) + Keychain (iOS). |
| Remote images (Coil 3)     | ✅ Implemented  | Singleton `ImageLoader` + Ktor network fetcher, both platforms. |
| Accessibility              | ✅ Implemented  | Roles/headings/live regions, 48 dp targets, shared contract tested on both platforms. |
| Light / dark theme         | ✅ Implemented  | Semantic colors follow the system; contrast‑checked palettes. |
| Localization (strings)     | ✅ Implemented  | All UI copy in `composeResources`; add `values-<lang>` to translate. |
| Maps (fuzzed pins)         | ⏳ Deferred     | List‑based discovery in v1 (Tech Doc §2).                   |
| Device GPS                 | 🚧 Stub        | Fixed Dhaka location placeholder (`// TODO`).               |
| Push delivery (FCM)        | 🚧 Needs config| Android code wired; add `google-services.json`. iOS APNs unbuilt. |
| iOS CI / TestFlight        | ⏳ Deferred     | Needs an Apple Developer account + macOS CI runner.         |
| A few ViewModel strings    | 🚧 Partial     | Composable copy is fully in resources; ~4 ViewModel/helper strings still inline. |

The client is feature‑complete against the v1 contract on **both platforms** and runs against any
backend that implements the documented API; the deferred items above (push accounts, iOS CI, maps,
real GPS) are the only intentional gaps. See [`docs/`](docs) for the phase‑by‑phase migration record.

---

## Contributing

Contributions are welcome. To add or change a feature:

1. Add the API in `:core:network` (DTOs + a Ktor service), the repository in `:core:data`,
   and the interface + use case(s) in `:core:domain` — all in `commonMain`.
2. Build the screen in its feature module as `Contract / ViewModel / Screen` + a navigation
   graph extension and a Koin module, following the patterns above. Keep it in `commonMain`;
   only reach for `androidMain`/`iosMain` for a real platform capability.
3. Keep the dependency rule intact (features depend only on `:core:*`; never on each other).
4. Run `./gradlew assembleDebug test :app:lint` and confirm iOS still links with
   `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` before opening a PR. Match the existing
   Kotlin style and keep modules' `build.gradle.kts` on the convention plugins.

Please open an issue to discuss larger changes first.

---

## License

Released under the **MIT License** — see [`LICENSE`](LICENSE). © NearAid contributors.
