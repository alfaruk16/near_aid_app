# NearAid — Résumé Evidence Report

_Generated on 2026-07-30. Every figure is drawn from the codebase and was **measured on this
checkout**. Caveats are stated inline so each claim survives scrutiny._

**NearAid** is a **Kotlin-native Android** app (Hilt, Retrofit/OkHttp, Room, Jetpack Compose)
following **MVI + Clean Architecture**, with a **BLE proximity-handoff** feature
(`:core:proximity`), a `:benchmark` Macrobenchmark module, JaCoCo coverage, and a broad
core-module test suite.

---

## 1. Module count + multi-module structure

**18 Gradle project modules** (`settings.gradle.kts`) **+ a `build-logic` composite build**.

| Layer | Modules |
|---|---|
| App | `:app` |
| Core (10) | `:core:common` · `:core:model` · `:core:designsystem` · `:core:navigation` · `:core:datastore` · `:core:network` · `:core:database` · `:core:domain` · `:core:data` · **`:core:proximity`** (BLE) |
| Feature (6) | `:feature:auth` · `:feature:discovery` · `:feature:post` · `:feature:activity` · `:feature:messages` · `:feature:profile` |
| Performance | `:benchmark` (com.android.test — Macrobenchmark) |
| Build logic | `build-logic/convention` — `nearaid.android.application(.compose)`, `.library(.compose)`, `.feature`, `.hilt`, `.room`, `jvm.library` |

Structure: **MVI + Clean Architecture**, layered feature → domain → data → core, dependencies wired
through custom Gradle **convention plugins**.

---

## 2. Full stack (from `gradle/libs.versions.toml` + build files)

| Area | Technology (version) |
|---|---|
| **Build / language** | Kotlin **2.0.21**, KSP 2.0.21-1.0.27, AGP **8.7.3**, Java target **17**, Gradle version catalog + convention plugins |
| **UI** | Jetpack **Compose** (BOM 2024.12.01), Material 3, Compose compiler plugin, Material Icons Extended, Coil 2.7.0, Splashscreen 1.0.1 |
| **Navigation** | Navigation-Compose **2.8.5** (type-safe routes) |
| **DI** | **Hilt 2.52** + hilt-navigation-compose 1.2.0 |
| **Networking** | **Retrofit 2.11.0** + **OkHttp 4.12.0** (+ logging), kotlinx.serialization JSON 1.7.3; **OkHttp WebSocket** for realtime chat |
| **Async** | **Kotlin Coroutines 1.9.0** + Flow |
| **Database / storage** | **Room 2.6.1** (KSP), **DataStore Preferences 1.1.1** |
| **Push** | **Firebase** BOM 33.7.0 (Cloud Messaging / FCM) |
| **Debug tooling** | **LeakCanary 2.14** |
| **Testing** | JUnit4 4.13.2, **MockK 1.13.13**, **Turbine 1.2.0**, coroutines-test 1.9.0, **Robolectric 4.14.1**, Compose UI test |
| **Proximity / BLE** | **`android.bluetooth.le`** `BluetoothLeAdvertiser` + `BluetoothLeScanner` (`:core:proximity`) for in-person handoff confirmation |
| **Coverage** | **JaCoCo 0.8.12** — `configureJacoco()` convention with a logic filter, per-module `jacocoTestReport`; **84% core-logic line coverage** |
| **Performance** | **AndroidX Macrobenchmark 1.3.3** + UiAutomator 2.3.0 — `:benchmark` startup module |
| **CI/CD** | **GitHub Actions** (green on `main`) — `.github/workflows/ci.yml` runs build (`assembleDebug`) → `testDebugUnitTest` → `jacocoTestReport` + Android `lint` on every push/PR to `main`/`develop`; `release.yml` publishes a tagged-release APK. JDK 17 runner, Android SDK provisioned via `android-actions/setup-android`. |

---

## 3. What the app does & what shipped

**NearAid** (পাশের মানুষ — "the person beside you") is a **hyperlocal mutual-aid** Android client — a
two-sided board connecting people who need everyday help with nearby neighbours willing to give.
**No money** changes hands. (Source: `README.md`.)

**Shipped feature modules** (all six present): **`auth`** (phone-OTP, profile, ID badge) ·
**`discovery`** (two-sided Needs/Offers, filters, distance-sorted, privacy-fuzzed location) ·
**`post`** (request/offer) · **`activity`** (claim → **deliver → confirm** → rate; includes the
merged two-step handoff fix — `ClaimStatus.DELIVERED`, nested `/me/claims` `MyClaimDto`, owner-side
handoff) · **`messages`** (realtime 1:1 chat over WebSocket + safety bar) · **`profile`** (trust,
ratings, verification, Bangla/English, report/block).

**BLE proximity-confirmed handoff (`:core:proximity`):** the "Mark delivered" step is gated on a
short-range **Bluetooth LE** check — both devices on a claim derive the same 4-byte token from the
claim id (FNV-1a), one advertises while the other scans, and a near-enough RSSI match confirms the
two people are physically together. Foreground-only; degrades gracefully (BLE off → proceed;
can't confirm → manual-confirm fallback). The receiving party can advertise ("I'm here to receive").
Includes the hardware fix: service-data-only advertisement under the 31-byte legacy limit + a
fail-fast on the advertise-start callback.

**Cross-cutting:** FCM push, accessibility contract (TalkBack roles/labels/headings, 48 dp targets,
live-region announcements, automated Compose a11y test), light/dark semantic theming,
localization-ready `strings.xml` (incl. Bangla).

**Deferred:** a dedicated map view (list-based discovery in v1). Backend is external
(Django + OpenAPI); this repo is the client.

---

## 4. Authorship & attribution — **solo**

```
$ git shortlog -sne HEAD
… faruk <alfarukemail@gmail.com> + Abdullah Al Faruk <alfarukemail@gmail.com>  # one person, two display names
# git log --author="alfarukemail@gmail.com" | wc -l == the full commit count — every commit resolves to this one email
```

**Every commit is yours — 100% solo authorship, no team commits.**

Effort concentration (commits touching each module): `feature:activity` 7 · `feature:messages` 7 ·
`feature:profile` 7 · `app` 6 · `core:network` 6 · `feature:auth`/`discovery`/`post` 6 each.

--- 

## 5. Hard evidence for metrics (measured on this branch)

| Metric | Value | Evidence / caveat |
|---|---|---|
| **Test suite** | **249 `@Test` across 46 files** | JUnit4 + MockK + Turbine + Robolectric + Compose UI test. Covers all 6 feature ViewModels + every `core:data` repository & mapper + every `core:domain` use case + `core:common` MVI base + `core:network` interceptors/authenticator + `core:proximity` token/match predicate + the BLE deliver-gate + `app` MainViewModel + `core:designsystem` a11y. |
| **Coverage — core logic** | **84.0% line** (1,322 / 1,573); branch 69.6% | **JaCoCo 0.8.12** with a **logic filter** (`./gradlew jacocoTestReport`) — excludes what isn't unit-testable: Compose UI (screens/theme/components), generated Hilt/Dagger + Room `*_Impl` + serializer stubs, DI wiring, wire DTOs, nav markers, entities/DAOs, `R`/`BuildConfig`, Android framework entry points. Reflects the testable logic surface: ViewModels, repositories, use cases, mappers, interceptors, utilities. |
| **Coverage — by module** (logic-filtered line) | features **98–100%** · `app` **100%** · `core:domain` **90.6%** · `core:data` **90.3%** · `feature:activity` **73.3%** · `core:common` **61.8%** · `core:network` **48.1%** · `core:proximity` **14.5%** | The two BLE-related dips are honest: `core:proximity` (14.5%) is mostly the `BluetoothLeAdvertiser`/`Scanner` radio flow, which **needs two real phones** (the pure `isHandoffMatch` predicate + token derivation are unit-tested); `feature:activity` (73.3%) has the receiver advertise-loop branches uncovered. `core:network`'s remainder is the WebSocket `ChatSocket`. |
| **Coverage — whole-repository** | **much lower (~29%), not the headline** | Including Compose UI screens (which need instrumented/Compose tests, not JVM unit tests) the whole-repo line figure is ~29%. The 84% figure is explicitly the **core-logic** slice — state the exclusion basis when citing. |
| **Startup — cold (physical device)** | **~1.59 s** median TTID (min 1.54, max 1.70, 5 iters) | **AndroidX Macrobenchmark** `StartupTimingMetric`, `:benchmark` module, measured on this checkout on a **physical Nokia 2.4 (API 31, entry-level)**, `CompilationMode.None()` (no baseline profile, animations disabled). Real-device number; a low-end handset with no baseline profile, so a conservative worst-case. |
| **Startup — warm (physical device)** | **~370 ms** median TTID (min 364, max 396, 5 iters) | Same device/run as above. |
| **Startup — emulator reference** | **~302 ms cold / ~112 ms warm** | Same benchmark on an API-37 emulator (`sdk_gphone16k_arm64`) — faster host CPU; a comparison point, not the headline. |
| **Build-time — cold** | **~16 s** `:app:assembleDebug` | `./gradlew clean` then `:app:assembleDebug --no-build-cache --profile`; profile HTML under `build/reports/profile/`. |
| **Build-time — incremental** | **~1 s** (no-op, all up-to-date) | Re-run of `:app:assembleDebug` with no changes. |

> Honesty guardrails: the **84%** coverage headline is the **core-logic** slice (JaCoCo logic
> filter — Compose UI, generated code, DI, DTOs, and Android framework entry points excluded);
> whole-repository line coverage including UI is ~29%. Always cite it as "core logic" with the
> exclusion basis. The BLE radio flow (`:core:proximity`) is not unit-tested — it needs two phones;
> only its pure match predicate is. Startup was measured on this checkout on a **physical Nokia 2.4
> (API 31, entry-level)** — **~1.59 s cold / ~0.37 s warm**, `CompilationMode.None` (no baseline
> profile); a conservative low-end figure (the API-37 emulator hit ~0.30 s cold). Cite with the
> device + no-baseline-profile caveat. Build-time is a local single-run measurement, not a CI average.

---

## 6. Team-size signal

No CODEOWNERS, no CONTRIBUTORS; `git shortlog -sne HEAD` = **1 person**. **Team size = 1 (solo).**

---

## Ready-to-paste résumé bullets (all measured above)

- Built **NearAid**, a hyperlocal mutual-aid Android app, as an **18-module** Gradle project
  (**MVI + Clean Architecture**) with **Jetpack Compose**, **Hilt**, **Retrofit/OkHttp**, **Room**,
  **DataStore**, and **Coroutines/Flow**, wired through custom Gradle **convention plugins**.
- Delivered six feature modules end to end — phone-OTP auth, two-sided discovery, post, a full
  claim → **deliver → confirm** → rate flow, **realtime WebSocket chat**, and profile/trust.
- Engineered a **BLE proximity-confirmed in-person handoff** (`BluetoothLeAdvertiser`/`Scanner`,
  RSSI-gated, foreground-only) so a delivery is confirmed only when both devices are physically
  together — degrading gracefully to manual confirm when Bluetooth is unavailable.
- Wrote **249 unit tests** (JUnit4/MockK/Turbine/Robolectric) and set up **JaCoCo** with a logic
  filter — **84% line coverage of core logic** (ViewModels, repositories, use cases, mappers,
  OkHttp interceptors, the proximity match predicate; 1,322/1,573), covering every `core:data`
  repository and `core:domain` use case; ~29% whole-repo including Compose UI.
- Stood up **AndroidX Macrobenchmark** cold/warm startup measurement on a **physical device** —
  **~1.6 s cold / ~0.37 s warm** time-to-initial-display on an entry-level Nokia 2.4 (API 31, no
  baseline profile — a conservative low-end figure; ~0.30 s cold on an API-37 emulator).
- Kept builds fast in an 18-module setup — **~16 s cold** `assembleDebug`, ~1 s incremental
  (Gradle configuration-cache + convention plugins).
- Built an **accessibility contract** into the design system (TalkBack roles/labels/headings, 48 dp
  targets, live-region announcements) enforced by an automated Compose test.
- Set up **GitHub Actions CI/CD** — build, unit tests, JaCoCo coverage and Android lint on every
  push/PR (green on `main`), plus a tagged-release APK pipeline.
- Shipped the app **solo** — every commit single-authored.

---

## Appendix — how each number was produced (reproduce)

- **Coverage:** `./gradlew jacocoTestReport` → per-module `build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml` (logic-filtered via `configureJacoco()`); summed the LINE counters = **1,322/1,573 = 84.0%** core logic.
- **Startup:** `ANDROID_SERIAL=<device> ./gradlew :benchmark:connectedBenchmarkAndroidTest` → `benchmark/build/outputs/connected_android_test_additional_output/.../com.nearaid.benchmark-benchmarkData.json` (this checkout: physical Nokia 2.4, API 31, animations disabled; `ANDROID_SERIAL` targets a specific device when several are attached).
- **Build-time:** `./gradlew clean && ./gradlew :app:assembleDebug --no-build-cache --profile` → `build/reports/profile/profile-*.html`.
- **BLE:** `:core:proximity` unit tests (`HandoffTokenTest` — token derivation + the `isHandoffMatch` predicate) via `./gradlew :core:proximity:testDebugUnitTest`; the deliver-gate ViewModel branches via `:feature:activity:testDebugUnitTest`. The `BluetoothLeAdvertiser`/`Scanner` radio flow needs two real devices, so it sits outside the JVM unit-test suite — **proven on hardware**: two physical phones (Redmi 23053RN02A + Nokia 2.4) each resolved `ProximityResult.Confirmed` off the other over real BLE. The on-device harness (`BleProximityHardwareTest` + `scripts/ble-proximity-proof.sh`, which installs the androidTest APK on both phones and runs `am instrument` in parallel) lives on the `feature/ble-proximity-handoff` branch, not `main`. (Note: every scanning phone needs system **Location** on, or Android silently drops BLE scan results.)
- **CI/CD:** GitHub Actions on `main`, passing — `.github/workflows/ci.yml` + `release.yml` (see
  `docs/ci-cd.md`). Runs on every push/PR to `main`/`develop`; check the **Actions** tab for the
  latest conclusion. (Two runner-only fixes were needed after the initial merge: provisioning the
  Android SDK via `android-actions/setup-android`, and stripping the local macOS `org.gradle.java.home`
  pin from `gradle.properties` on the runner.)
- **Not evidenced in this repo:** an iOS build.
