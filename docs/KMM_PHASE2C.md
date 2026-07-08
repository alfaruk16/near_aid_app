# KMM Phase 2c — :core:data → commonMain (done)

Part of the [KMM migration roadmap](KMM_MIGRATION_ROADMAP.md). Completes roadmap **Phase 2**
("share data access"): after [2a](KMM_PHASE2A.md) (Ktor + DataStore) and [2b](KMM_PHASE2B.md)
(Room-KMP), 2c moves `:core:data` — the mappers and every `RepositoryImpl` — into `commonMain`.
**Milestone reached: the entire non-UI stack is shared Kotlin; the Android app runs fully on it.**

## What changed
- `:core:data` → **KMP** (`nearaid.kmp.library`). All 9 `RepositoryImpl`s, the mappers
  (`Mappers`, `CacheMappers`, `Wire`) and `DataModule` moved to `commonMain` **unchanged** — they
  already depended only on interfaces from the now-multiplatform network/database/datastore/domain
  modules.
- **One JVM leak fixed:** `UserRepositoryImpl.submitVerification` read the upload file via
  `java.io.File`. Replaced with **okio** (`FileSystem.SYSTEM.read(path) { readByteArray() }`), which
  is common code (JVM + Native), feeding the same `ByteArray + fileName` into the Ktor multipart call.
- Deps switched to KMP artifacts (`kotlinx-coroutines-core` instead of `-android`); `okio` added.

## Verified — both platforms
- `:app:assembleDebug` + `testDebugUnitTest` green.
- `:core:data:compileKotlinIosSimulatorArm64` green (compiles alongside `:core:model` and
  `:core:database` for Native) — the full repository layer now builds for iOS.

## Next
Phase 3 — share presentation: move each `feature/*` `*Contract.kt` + `*ViewModel.kt` (+ Koin module)
to `commonMain`, keeping the Android Compose `Screen.kt`s on the Android target.
