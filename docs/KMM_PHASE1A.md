# KMM Phase 1a — Hilt → Koin (done)

Part of the [KMM migration roadmap](KMM_MIGRATION_ROADMAP.md). Roadmap Phase 1 is split into
**1a: the app-wide DI swap** (this PR) and **1b: moving the pure core to `commonMain`** (next).
All modules stay Android libraries here — no source-set moves — so the DI change is de-risked on
its own. Hilt is Android/JVM-only and can't cross into `commonMain`; Koin can, so it replaces Hilt
now and carries over unchanged when the core goes multiplatform.

## What changed
- **DI library:** Hilt → **Koin 4.0.3** (`koin-core`, `koin-android`, `koin-androidx-compose`).
  No KSP needed for Koin; KSP remains only for Room (`:core:database`).
- **build-logic:** deleted `AndroidHiltConventionPlugin`; the feature convention plugin now brings
  `koin-androidx-compose` instead of `hilt` + `hilt-navigation-compose`.
- **Every `@Module` → a Koin `module {}`** (`commonModule`, `networkModule`, `dataStoreModule`,
  `databaseModule`, `dataModule`, `domainModule`, `appModule`, and one per feature).
- **`@Inject`/`@Singleton`/`@HiltViewModel` removed** from ~18 classes, 27+ use cases, 15 ViewModels.
  Screens use `koinViewModel()`; the Activity uses `by viewModel()`; the FCM service uses `by inject()`.
- **Qualifiers replaced:** `@BaseUrl`/`@WsUrl` → a `NetworkConfig(baseUrl, wsUrl)` data class provided
  by `:app` from `BuildConfig`; `@Dispatcher(IO)` → Koin `named("io")` dispatchers in `commonModule`.
- **Cycle break:** `TokenAuthenticator` takes a `() -> AuthApi` provider (was `dagger.Lazy<AuthApi>`),
  wired as `single { TokenAuthenticator(get()) { get<AuthApi>() } }` to break OkHttp ⇄ Retrofit ⇄ AuthApi.
- **Startup:** `NearAidApplication.onCreate` calls `startKoin { androidContext(...); modules(...) }`;
  Compose is wrapped in `KoinAndroidContext`.

## Verified
- `./gradlew :app:assembleDebug` — green.
- `./gradlew testDebugUnitTest` — all modules pass.
- Ran on an emulator: app boots, the Koin graph resolves (splash → Welcome/Phone screens render,
  ViewModels + repositories + the cyclic `TokenAuthenticator` all resolve), no `NoDefinitionFound`/
  crash in logcat.
- `grep` for `dagger|javax.inject|Hilt*|@Singleton|@InstallIn` over `app/core/feature` `.kt` → zero.

## Next (Phase 1b)
Move `:core:domain` / `:core:navigation` / `:core:common` to `commonMain` (KMP): KMP
lifecycle-viewmodel for `MviViewModel`, rewrite `TimeFormat` (JVM `SimpleDateFormat` →
kotlinx-datetime / expect-actual), and make the IO dispatcher `expect`/`actual`.
