# KMP Phase 5 — platform edges & polish (partial: code-completable edges)

Part of the [KMP migration roadmap](KMP_MIGRATION_ROADMAP.md). Phases 0–4 shared all logic **and** the
Compose Multiplatform UI. Phase 5 closes the platform-edge gaps the earlier phases deferred. This pass
completes the **code-completable** edges — the ones finishable without external accounts. Push/APNs and
iOS CI/TestFlight stay deferred (they need Firebase/Apple credentials and are v1 product decisions).

## What changed
- **Coil 3 singleton `ImageLoader` (remote images now load, both platforms).** `App()` (`:shared`,
  commonMain) calls `setSingletonImageLoaderFactory { ImageLoader.Builder(it).components { add(
  KtorNetworkFetcherFactory()) }.build() }`. Coil 3 ships no network fetcher by default, so `AsyncImage`
  (avatars, listing photos) previously resolved nothing remote and fell back to placeholders. The Ktor
  fetcher uses each platform's already-linked engine (OkHttp / Darwin). `:shared` gained `coil-compose3`
  + `coil-network-ktor3`.
- **iOS image picker → real `PHPickerViewController`** (`feature/profile/iosMain/ImagePicker.ios.kt`,
  replacing the empty-lambda stub). Presents a single-image picker from the front-most view controller,
  loads the picked bytes via `NSItemProvider.loadDataRepresentationForTypeIdentifier("public.image")`,
  writes them to `NSTemporaryDirectory()/<fileName>`, and returns the path on the main queue — mirroring
  the Android `GetContent` + cache-copy actual. The delegate is held strongly (the picker keeps only a
  weak ref). PHPicker needs **no** `NSPhotoLibraryUsageDescription` (out-of-process).
- **Secure token storage (`expect`/`actual` via the platform Koin modules).** New
  `SecureTokenStore` interface + `StoredSession` in `:core:datastore` commonMain. `AuthPreferencesDataSource`
  now persists the JWT pair through it instead of plaintext DataStore, mirroring the value into a
  `MutableStateFlow` so the reactive `tokens`/`isLoggedIn` surface is unchanged. Actuals:
  **Android** `EncryptedSharedPreferences` (Keystore-wrapped AES-GCM, `androidx.security:security-crypto`);
  **iOS** the **Keychain** (`kSecClassGenericPassword` items via the Security framework). Wired as
  `single<SecureTokenStore>` in each `dataStorePlatformModule`. Non-sensitive prefs
  (`UserPreferencesDataSource`: language/radius) stay in DataStore.
- **A11y contract shared to `commonMain` + tested on both platforms.** New `A11y` object (designsystem
  commonMain) is the single source of truth for the 48dp min touch target and the "clickable node is
  labeled" rule; production modifiers (`accessibleClickable`, chip/segmented-tab min sizes) and the tests
  now reference it. New `commonTest` `A11yContractTest` runs on Android (JVM) **and iOS native**; the
  Android Robolectric `AccessibilityChecksTest` consumes the same contract against a real semantics tree.

## Verified — both platforms
- `:app:assembleDebug` + `:core:designsystem:testDebugUnitTest` green (Android build + refactored a11y +
  shared-contract tests, secure-storage refactor, Coil wiring).
- `:core:datastore` + `:feature:profile` `compileKotlinIosSimulatorArm64` green (Keychain store + PHPicker).
- `:core:designsystem:iosSimulatorArm64Test` green — the shared a11y contract runs on iOS native.
- `:shared:linkDebugFrameworkIosSimulatorArm64` green — the full framework links (Coil singleton + secure
  store + picker + CMP UI + SKIE).

## Still deferred (need accounts / v1 product decisions — not code-blocked)
- **Push:** FCM (Android) is implemented but not initialized (no `google-services.json`); iOS APNs/Firebase
  bridge unbuilt. Both need a Firebase project — a deploy step, intentionally out of scope for v1.
- **iOS CI + TestFlight:** need an Apple Developer account + a macOS CI runner.
- **Deep links / share sheet / permission edges** beyond the picker.
- **iOS rendering-based a11y** via `runComposeUiTest` (would let iOS exercise the CMP-resource-backed
  components the JVM Robolectric reader can't load). The a11y *rules* are now shared and tested on both
  platforms; the rendering harness stays Android-only for now.
