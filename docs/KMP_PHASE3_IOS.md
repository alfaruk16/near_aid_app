# KMP Phase 3 milestone — iOS drives the shared ViewModels (done)

Part of the [KMP migration roadmap](KMP_MIGRATION_ROADMAP.md). [Phase 3](KMP_PHASE3.md) made every
ViewModel shared Kotlin; this milestone wires **SwiftUI to that shared code**, closing the
roadmap's **logic-only KMP finish line**: iOS runs the same ViewModels, repositories, network and DB
as Android, with a native SwiftUI front end.

## What changed
- **New `:shared` umbrella module** produces the `Shared` iOS framework (moved off `:core:model`,
  where Phase 0 first stood it up). It `api`-exports the shared modules Swift needs to see —
  `:core:model`, `:core:common` (the `MviViewModel` base), `:core:domain`, `:core:navigation` and all
  six `:feature:*` modules — and privately depends on data/network/datastore/database to assemble the
  graph.
- **`doInitKoin(baseUrl, wsUrl)`** (`shared/Koin.kt`) — the iOS counterpart of the Android app's
  `startKoin`: the same commonMain modules, `NetworkConfig` supplied from Swift (Android reads it from
  `BuildConfig`), and the DataStore/Room platform modules resolving their iOS `actual`s (no
  `androidContext`). `SharedViewModels` exposes each ViewModel as a Koin-resolved factory for Swift.
- **SKIE** (`co.touchlab.skie` 0.10.4) applied to `:shared` so Swift sees Kotlin `StateFlow` as an
  `AsyncSequence` and `suspend` funs as `async` — no hand-written bridging.
- **SwiftUI** (`iosApp`): `iOSApp` calls `KoinKt.doInitKoin(...)` at launch; `ContentView` creates the
  shared `PhoneViewModel`, `for await`s its `state`, renders it, and forwards edits/taps to
  `onIntent`. (SKIE flattens Kotlin sealed members to top-level Swift types —
  `PhoneIntentPhoneChanged`, `PhoneIntentSubmit`.)
- The Xcode "Compile Kotlin Framework" build phase now runs `:shared:embedAndSignAppleFrameworkForXcode`.

## Verified — both platforms
- `:app:assembleDebug` green (Android unaffected by the framework move).
- `:shared:linkDebugFrameworkIosSimulatorArm64` green (umbrella exports + SKIE processing).
- `xcodebuild ... -sdk iphonesimulator` **BUILD SUCCEEDED** — the full app compiles and links the
  framework.
- **Ran on the iPhone 16 simulator:** the app boots, `doInitKoin` resolves the whole graph on iOS
  (Ktor Darwin engine, Room bundled SQLite driver, DataStore in `NSDocumentDirectory`) with no crash,
  and the SwiftUI screen renders the shared `PhoneViewModel` — the "Request OTP" button is disabled
  because the shared `PhoneState.canSubmit`, observed live via SKIE, is `false` for an empty number.

## What's next (optional, beyond the finish line)
- **Phase 4 (Full Compose Multiplatform):** share the UI too (`:core:designsystem` → CMP, Screens to
  `commonMain`). The `compose-runtime` already sits in the feature `commonMain`s.
- **Phase 5 (platform edges + CI):** `expect`/`actual` FCM/APNs push, deep links, secure token
  storage (Keychain/Keystore), and iOS CI (`xcodebuild test` + TestFlight).
