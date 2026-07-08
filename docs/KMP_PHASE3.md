# KMP Phase 3 — share presentation (ViewModels) (done)

Part of the [KMP migration roadmap](KMP_MIGRATION_ROADMAP.md). Phase 3 moves every feature's MVI
surface — `*Contract.kt`, `*ViewModel.kt` and the Koin `module {}` — into `commonMain`, so all 15
ViewModels are shared Kotlin compiling for Android and iOS. The Android Compose `Screen.kt`s stay on
the Android target, bound to the now-shared ViewModels. **This is the logic-only KMP finish line for
the shared code**; wiring Swift to it is the [Phase 3 milestone](KMP_PHASE3_IOS.md).

## What changed
- New **`nearaid.kmp.feature`** convention plugin: KMP module (`androidTarget` + iOS) with the Kotlin
  Compose compiler + `com.android.library` `buildFeatures.compose` **enabled on the Android target
  only** (Compose Multiplatform is a later, optional phase).
- All 6 feature modules (`auth`, `discovery`, `post`, `activity`, `messages`, `profile`) converted:
  - `commonMain` ← `*Contract.kt`, `*ViewModel.kt`, `di/*Module.kt`. No source changes were needed —
    the ViewModels already extend the shared `MviViewModel` (KMP `androidx.lifecycle`, from Phase 1b)
    and use `viewModelScope`; the Koin `viewModelOf` DSL comes from **`koin-core-viewmodel`** (Koin 4
    multiplatform ViewModel support, previously pulled transitively via `koin-android`).
  - `androidMain` ← `Screen.kt` composables + `navigation/` + `res/` (strings).
  - `androidUnitTest` ← the existing ViewModel tests (mockk/turbine are JVM-only, so they stay on the
    Android target and keep passing).
- **`compose-runtime` (Compose Multiplatform, runtime only — no UI toolkit) added to `commonMain`.**
  The Kotlin Compose compiler plugin instruments *every* target of the module; on iOS it aborts
  unless a Compose runtime is on the classpath. The platform-agnostic runtime satisfies it without
  pulling any UI into iOS (the shared code contains no `@Composable`s).
- Compose BOM in a KMP source set is referenced as `project.dependencies.platform(...)` — the KMP
  `KotlinDependencyHandler.platform(...)` can't take a version-catalog provider directly.

## Verified — both platforms
- `:app:assembleDebug` + `testDebugUnitTest` green (all ViewModel tests run from `androidUnitTest`).
- `:feature:*:compileKotlinIosSimulatorArm64` green for all six features — every ViewModel + Contract
  + Koin module compiles for iOS.

## Next
[Phase 3 milestone](KMP_PHASE3_IOS.md) — export the shared modules through the `Shared` umbrella
framework (+ SKIE for ergonomic Swift `Flow`/suspend interop) and drive a shared ViewModel from
SwiftUI, closing the logic-only KMP loop.
