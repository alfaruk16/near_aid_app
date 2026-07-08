# KMP Phase 1b — pure core → commonMain (done)

Part of the [KMP migration roadmap](KMP_MIGRATION_ROADMAP.md). Completes roadmap **Phase 1**:
after 1a made DI portable (Hilt→Koin), 1b moves the pure core — `:core:navigation`,
`:core:common`, `:core:domain` — into `commonMain`, so the MVI base, error types, routes,
repositories and 40 use cases are shared Kotlin that compiles for **Android and iOS**. `:core:model`
was already KMP (Phase 0). Milestone reached: *the Android app runs unchanged on a shared pure core.*

iOS *integration* (exporting these via the `Shared` framework, driving ViewModels from Swift) is
Phase 3 — 1b only proves `commonMain`-readiness.

## What changed
- **`:core:navigation`** → KMP; `Routes.kt` (pure `@Serializable`) moved to `commonMain` unchanged.
- **`:core:domain`** → KMP; all repositories + use cases + `DomainModule` moved to `commonMain` unchanged.
- **`:core:common`** → KMP:
  - `MviViewModel` now backed by the multiplatform **`org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.8.2`** (same `androidx.lifecycle` package → source unchanged; on Android it's still `androidx.lifecycle.ViewModel`, so `koinViewModel()`/`viewModelOf` keep working).
  - **`TimeFormat`** rewritten on **kotlinx-datetime 0.6.1** (was `java.text.SimpleDateFormat`) — same output strings, now common code.
  - **IO dispatcher** is `expect`/`actual` (`Dispatchers.IO` is JVM/Android-only): Android → `Dispatchers.IO`, iOS → `Dispatchers.Default`; exposed via `commonModule` `named("io")`.
  - `commonMain` deps switched to KMP artifacts (`kotlinx-coroutines-core`), Android-only `core-ktx`/`lifecycle-*` dropped from this module.
- Public-API deps (`lifecycle-viewmodel`, `coroutines-core`, `koin-core`) are `api` so `MviViewModel : ViewModel`, `Flow`/`StateFlow`, and the public `commonModule`/`domainModule` are visible to consumers.
- Tests (`DataResultTest`, `TimeFormatTest`, `PhoneNumberTest`) moved to `commonTest` with `kotlin.test`.
- **Consumers unchanged** — `:core:data`, `:core:network`, `:core:designsystem`, `:feature:*`, `:app` resolve each module's `androidTarget` variant automatically.

## Verified
- `:app:assembleDebug` — green.
- `testDebugUnitTest` — all modules pass (common/domain tests now run from `commonTest`).
- `:core:{navigation,common,domain}:compileKotlinIosSimulatorArm64` — green (commonMain-ready incl. `expect`/`actual` + kotlinx-datetime).
- `:core:common` & `:core:domain` `iosSimulatorArm64Test` — green (cross-platform parity, esp. `TimeFormat`).
- App boots on an emulator; Koin graph resolves; no crash/`NoDefinitionFound`.

## Next
Phase 2 — share data access: Retrofit→Ktor, Room→Room-KMP, DataStore-KMP, move `:core:data`/`:core:network`/`:core:database`/`:core:datastore` to `commonMain`.
