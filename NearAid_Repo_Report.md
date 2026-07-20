# NearAid — Repository Evidence Report

**Kotlin Multiplatform mutual-aid app (Android + iOS) · master-resume attribution audit**

Generated 2026-07-20 · branch `KMP` (post CI/quality merge) · every figure is drawn directly from the repo; coverage and performance are measured, nothing is inferred.

| Metric | Value |
| --- | --- |
| Gradle modules | **18** |
| Commits (all branches) | **43** |
| Contributor (solo) | **1** |
| Line coverage (Kover) | **18%** |
| Cold start (emulator) | **585 ms** |

---

## 1. Module structure

**18 modules** — NIA-style multi-module layout with a `build-logic` included build providing convention plugins (`nearaid.android.*`, `nearaid.kmp.*`, `nearaid.cmp.library`, `nearaid.jvm.library`, `nearaid.quality`, `nearaid.kover`). Source: `settings.gradle.kts`.

| Group | Modules |
| --- | --- |
| **Host (2)** | `:app` (Android Activity, thin) · `:shared` (iOS umbrella framework, exports to Xcode as `Shared`) |
| **Core (9)** | `:core:common` · `:core:model` · `:core:designsystem` · `:core:navigation` · `:core:datastore` · `:core:network` · `:core:database` · `:core:domain` · `:core:data` |
| **Feature (6)** | `:feature:auth` · `:feature:discovery` · `:feature:post` · `:feature:activity` · `:feature:messages` · `:feature:profile` |
| **Performance (1)** | `:benchmark` (AndroidX Macrobenchmark — cold/warm startup; added during this audit, see §5) |

---

## 2. Full stack — from `libs.versions.toml`

| Category | Technology |
| --- | --- |
| **Language / platform** | Kotlin 2.1.20 Multiplatform (Android + iOS) · Compose Multiplatform 1.7.3 · AGP 8.7.3 · Java 17 · KSP 2.1.20-1.0.32 (KSP1) |
| **UI** | Jetbrains Compose (shared UI), Material3, Navigation Compose, Coil 2/3 |
| **Architecture** | MVI + Clean Architecture · multi-module + convention plugins |
| **DI** | Koin 4.0.3 (migrated off Hilt — commit `a309abc`) |
| **Networking** | Ktor 3.0.3 client (OkHttp + Darwin engines), WebSockets, content-negotiation, auth, logging, mock · kotlinx.serialization 1.7.3 |
| **Async** | Coroutines 1.9.0 + Flow · kotlinx-datetime 0.6.1 |
| **DB / persistence** | Room-KMP 2.7.1 (bundled SQLite 2.5.1) · DataStore 1.1.1 · androidx.security-crypto (secure tokens) · Okio |
| **iOS interop** | SKIE 0.10.4 |
| **Push** | Firebase BOM 33.7.0 / messaging wired, pending config |
| **Testing** | JUnit4 · androidx-junit · Espresso · Robolectric 4.14.1 · MockK 1.13.13 · Turbine 1.2.0 · coroutines-test |
| **Quality / coverage** | detekt 1.23.7 · Spotless 6.25.0 / ktlint 1.3.1 · Kover 0.9.1 (coverage) · Macrobenchmark 1.3.3 + UiAutomator 2.3.0 (startup) |
| **Debug tooling** | LeakCanary 2.14 |
| **CI / CD** | GitHub Actions — `ci.yml` (unit tests, lint, assembleDebug, detekt, Spotless, iOS shared tests + Xcode build on macOS runner) · `release.yml` (signed APK → Firebase App Distribution on `v*` tags) |

---

## 3. What the app does & what shipped

**NearAid** (পাশের মানুষ, *"the person beside you"*) — a hyperlocal mutual-aid app connecting people who need everyday help (food, clothes, medicine, shelter) with nearby neighbours. A two-sided board (Seeker/Helper, Giver/Recipient). No money changes hands — it is a discovery + coordination layer; handoff happens offline. One shared Kotlin + Compose codebase runs on both Android and iOS.

**Shipped** (README features + commit history):

- Phone/OTP auth + profile setup
- Two-sided discovery (Needs/Offers, filters, banded/fuzzed distance)
- Post request / offer flow
- Claim → chat → deliver → confirm → rate
- Realtime 1:1 chat over WebSocket
- Messages tab, profile / trust / ratings
- Report + block safety
- Dark theme (semantic color system)
- Accessibility (TalkBack / VoiceOver)
- Bangla / English localization
- Shared UI + logic on Android & iOS

**Deferred / not fully shipped** (state honestly):

- **deferred** — Map view — replaced by list-based discovery in v1
- **pending** — FCM push — wired but pending Firebase config
- **deferred** — iOS release CD — pending Apple Developer account (commit `1e2d33d`)

---

## 4. Commit attribution

```
# requested command
$ git log --author="alfarukemail@gmail.com" --oneline | wc -l    → 36 (this branch)
$ git shortlog -sne --all                                        → 43 faruk <alfarukemail@gmail.com>
```

**100% solo repository.** Every commit on every branch (`main`, `KMP`, `ci/setup-cicd`) is yours — 43 of 43 commits, single author. There are no team commits.

| Arc | Coverage |
| --- | --- |
| **Foundation** | First commit, gitignore, unit tests (app / core / features) |
| **Android polish** | Dark theme, accessibility, string resources |
| **KMP migration** (largest arc) | Phase 0 skeleton → Hilt→Koin → pure core to commonMain → Retrofit→Ktor + DataStore-KMP → Room-KMP + `:core:data` → ViewModels to commonMain → iOS drives shared ViewModels (SKIE) → Compose Multiplatform shared UI → platform edges (Coil, iOS picker, secure tokens, shared a11y) |
| **Chat / realtime** | WebSocket failure handling, DTO field mapping fixes |
| **CI / CD** | PR validation, detekt + Spotless, Android release CD |

**Attribution guidance:** claim full individual ownership. Frame as *"sole developer / individual project"* — do **not** use "led a team" or "collaborated"; there is no evidence of other contributors.

---

## 5. Metric evidence

| Metric | Status | What the repo actually contains |
| --- | --- | --- |
| **Test coverage %** | now evidenced | 18.0% overall line coverage (1,176 / 6,541 lines) — measured with Kotlinx Kover 0.9.1, wired as a `nearaid.kover` convention plugin aggregating all modules over the JVM/Android unit tests (`./gradlew koverXmlReport`). Also 13.9% branch, 21.3% method, 34.1% class. Composables and generated code are excluded so the figure reflects testable logic. Coverage is uneven — see breakdown below. Cite as *"~18% line coverage (Kover), concentrated in ViewModels & core"*, not as broad coverage. |
| **Startup time** | now evidenced | Cold start 585 ms median (531–758 ms), warm start 116 ms median (98–179 ms) — `timeToInitialDisplayMs`, 5 iterations each, AndroidX Macrobenchmark 1.3.3 (`StartupTimingMetric`) against a new profileable, non-minified benchmark app variant. Caveat: run on an Android emulator (API 37, `suppressErrors=EMULATOR`) — not representative of a specific physical device; cite as an emulator baseline or re-run on a phone. An `adb am start -W` cross-check on the debug build gave ~1.0–1.45 s (debug + LeakCanary overhead, expected higher). |
| **Build time** | measured, noisy | Gradle `--profile` reports generated (Gradle 8.11.1, 402 tasks). Configuration ~1.1–1.3 s; clean `:app:assembleDebug` ranged ~20 s to ~1m40 s wall-clock across runs depending on daemon/machine warmth, and a no-op re-ran 322/402 tasks (no configuration cache). Too variable to cite a single build-time figure honestly — report the range with the caveat, or stabilize (configuration cache + clean-room run) before quoting a number. |

**Coverage by module** (line %, Kover merged report) — concentrated in ViewModels, the design system and core utilities; the data/persistence/model layers are effectively untested:

| Tier | Modules (line coverage) |
| --- | --- |
| **Tested (25–63%)** | `core:common` 62.9% · `core:designsystem` 41.2% · `feature:messages` 34.9% · `feature:discovery` 32.1% · `feature:activity` 31.1% · `feature:profile` 25.8% |
| **Light (5–25%)** | `feature:auth` 24.5% · `feature:post` 24.3% · `core:network` 7.3% · `core:domain` 5.2% |
| **Untested (0%)** | `core:database` · `core:data` · `core:model` · `core:datastore` · `core:navigation` · `app` · `shared` |

---

## 6. Team-size signal

**Solo.** No `CODEOWNERS` file exists. `git shortlog -sne --all` reports exactly one contributor (faruk). Every signal points to a single-developer project.

---

## 7. Resume honesty summary

**SAFE TO CLAIM (EVIDENCED):**

18-module KMP architecture · full stack above · sole authorship · CI/CD pipeline · WebSocket realtime chat · complete Android→KMP migration · shared Compose UI on Android + iOS · ~18% line coverage

---

## 8. Resume bullets — ready to paste

Every number below is backed by the evidence in this report. Pick 3–5; the emulator/coverage caveats are baked into the wording so the claims survive scrutiny.

### Project header

> **NearAid** — Kotlin Multiplatform mutual-aid app (Android + iOS) · *Sole developer* · Kotlin, Compose Multiplatform, Ktor, Koin, Room-KMP

### Bullets (impact-first)

- Sole developer of a hyperlocal mutual-aid app spanning **18 Gradle modules** on a single **Kotlin Multiplatform** codebase — shared business logic *and* Compose Multiplatform UI render on both Android and iOS from one source (43/43 commits authored solo).
- Architected an **MVI + Clean Architecture** multi-module setup with custom **build-logic convention plugins**, keeping 15 feature/core modules on consistent, DRY build config.
- Led a full **Android→KMP migration**: swapped Hilt→**Koin**, Retrofit→**Ktor** (incl. WebSocket realtime chat), and moved persistence to **Room-KMP** + DataStore with platform-secure token storage.
- Built a **CI/CD pipeline** (GitHub Actions): unit tests, Android lint, detekt + Spotless gates, iOS shared-framework tests + Xcode build on a macOS runner, and signed-APK release to Firebase App Distribution on version tags.
- Instrumented quality & performance tooling: **~18% line coverage** (Kotlinx Kover, concentrated in ViewModels & core modules) and startup benchmarking via **AndroidX Macrobenchmark** — ~0.6 s cold / ~0.12 s warm time-to-initial-display (emulator baseline).
- Shipped accessible, localized UI: TalkBack/VoiceOver semantics, a semantic light/dark color system, and Bangla/English string resources — validated by a shared cross-platform accessibility contract.

### Tech-stack line

> Kotlin Multiplatform · Compose Multiplatform · Coroutines/Flow · Ktor · Koin · Room-KMP · DataStore · kotlinx.serialization · SKIE · JUnit/MockK/Turbine/Robolectric · Kover · Macrobenchmark · detekt/Spotless · GitHub Actions · Gradle convention plugins

**Keep out** (unsupported by evidence): "high/comprehensive test coverage" (it's ~18%), any single build-time figure (measurements are noisy), device-specific startup numbers (only measured on emulator), and any "team/led a team" framing (solo project).

---

*All figures verified against the working tree on branch `KMP` at generation time. Sources: `settings.gradle.kts`, `gradle/libs.versions.toml`, `.github/workflows/{ci,release}.yml`, `README.md`, `git log` / `shortlog`, Kover merged report (`koverXmlReport`), Gradle `--profile` reports, and AndroidX Macrobenchmark output (`com.nearaid.benchmark-benchmarkData.json`). Coverage and startup are measured; build-time is measured but variable; nothing is inferred.*
