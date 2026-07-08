# KMM Phase 4 — Share the UI (Compose Multiplatform) (done)

Part of the [KMM migration roadmap](KMM_MIGRATION_ROADMAP.md). The optional Full-CMP phase: the
Android Compose UI becomes **one shared UI** rendering on Android and iOS. After this, ~all of NearAid
— logic *and* UI — is shared Kotlin; each platform is a thin host.

## What changed
- **Compose Multiplatform 1.7.3** adopted (matches the locked Kotlin 2.1.20; its runtime already
  linked in Phase 3). New `nearaid.cmp.library` convention = KMP + `org.jetbrains.compose` + the
  Kotlin Compose compiler, with `composeResources` for strings. Replaces the Jetpack-only
  `nearaid.kmp.feature`. The plugin is loaded via the root `apply false` so convention plugins can
  apply it.
- **`:core:designsystem` → CMP** (`commonMain`): theme/components move as-is (Material3 + foundation
  APIs are identical across CMP); **Coil → Coil 3** (`coil3.compose.AsyncImage`); `stringResource`
  → `org.jetbrains.compose.resources` + generated `Res`; strings → `composeResources/values`.
- **All 6 feature `Screen.kt`s + navigation → `commonMain`.** The only source churn per screen:
  `stringResource`/`R` → CMP `Res`, `koinViewModel` → `org.koin.compose.viewmodel`. Material3,
  foundation, `material.icons`, `lifecycle.compose` imports are unchanged. Navigation uses
  **JetBrains `navigation-compose`** (`org.jetbrains.androidx.navigation`) — same `androidx.navigation`
  API, so nav graphs compiled verbatim. ~214 strings across 7 `strings.xml` moved to `composeResources`.
- **App root → `:shared`** (now a CMP module): `App()` (root `NavHost` + theme + splash routing),
  `MainScreen` (bottom nav), `TopLevelDestination`, `MainViewModel`. `:shared/iosMain` adds
  `MainViewController()` = `ComposeUIViewController { App() }`.
- **Thin hosts:** Android `MainActivity` = `setContent { KoinAndroidContext { App() } }` (its
  NavHost/MainScreen/TopLevelDestination/MainViewModel deleted); iOS `ContentView` = a
  `UIViewControllerRepresentable` wrapping `MainViewController()`. One Compose tree, both platforms.
- **`expect`/`actual` image picker** (`VerificationScreen`): Android `GetContent` + cache-copy; iOS
  is a Phase 5 stub. **`formatOneDecimal`** added to `:core:common` (multiplatform `String.format("%.1f")`).

## Verified — both platforms
- `:app:assembleDebug` + `testDebugUnitTest` green (all ViewModel + a11y/Robolectric tests still run
  from `androidUnitTest`).
- Every module + feature compiles for `iosSimulatorArm64`; `:shared:linkDebugFrameworkIosSimulatorArm64`
  green (CMP UI + SKIE in one framework).
- `xcodebuild -sdk iphonesimulator` **BUILD SUCCEEDED**; ran on the iPhone 16 simulator — the shared
  Compose UI renders natively on iOS (see below).

## Known follow-ups (polish, not blockers)
- **Coil 3 network fetcher** isn't wired into a singleton `ImageLoader`, so remote avatars don't load
  yet (letter-fallback avatars work). One `setSingletonImageLoaderFactory` with `coil-network-ktor3`.
- **iOS image picker** stub → real `PHPickerViewController` (Phase 5, with push/secure-storage edges).
