# CI/CD

This document describes the CI/CD setup for **NearAid** (Kotlin Multiplatform:
Android `:app` + iOS `iosApp`/`:shared`) and the roadmap for extending it.

## Status at a glance

| Phase | Scope | State |
|-------|-------|-------|
| 0 | Make the build CI-portable | ✅ Done |
| 1 | PR validation (build + test + lint) | ✅ Done (`.github/workflows/ci.yml`) |
| 2 | Quality gates (detekt / spotless) | ✅ Done (detekt gating; spotless advisory) |
| 3 | CD — Android release (signing → Firebase App Distribution) | ✅ Done (needs secrets) |
| 4 | CD — iOS release (signing → TestFlight) | ⬜ Not started |

---

## Phase 0 — CI portability (done)

The committed `gradle.properties` **no longer hardcodes** `org.gradle.java.home`
(it previously pointed at a local Android Studio path, which breaks CI runners).
A JDK 17+ is now supplied per-environment:

- **CI:** `actions/setup-java` (Temurin 17) — see the workflow.
- **Local terminal:** set it in `~/.gradle/gradle.properties` (per-user, not
  committed) or export `JAVA_HOME`. Example:
  ```properties
  # ~/.gradle/gradle.properties
  org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home
  ```
- **Android Studio:** Settings → Build Tools → Gradle → Gradle JDK.

> If you clone fresh on a new machine and `./gradlew` can't find a JDK, this is
> why — add the property above to your global Gradle file.

---

## Phase 1 — PR validation (done)

Workflow: [`.github/workflows/ci.yml`](../.github/workflows/ci.yml). Triggers on
PRs into `main`, pushes to `main`, and manual `workflow_dispatch`. Superseded
runs on the same ref are cancelled automatically.

### `android` job (`ubuntu-latest`)
Cheap/fast; covers common + Android + JVM code.

| Step | Command | Why |
|------|---------|-----|
| Unit tests | `./gradlew testDebugUnitTest` | Runs feature/app ViewModel tests **and** `commonTest` (executed via the Android target) |
| Lint | `./gradlew :app:lintDebug` | AGP lint (uses the repo `lint.xml`) |
| Build | `./gradlew :app:assembleDebug` | Proves the Android app compiles |

Artifacts uploaded: test/lint HTML reports and the debug APK.

### `ios` job (`macos-latest`)
Required because Kotlin/Native + Xcode need macOS. Slower and uses more CI
minutes, so it's isolated in its own job.

| Step | Command |
|------|---------|
| iOS unit tests | `./gradlew :shared:iosSimulatorArm64Test` |
| Framework link | `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` |
| Xcode build | `xcodebuild build … -scheme iosApp … CODE_SIGNING_ALLOWED=NO` |

> **No secrets are required for Phase 1.** The `com.google.gms.google-services`
> plugin is not applied, so `google-services.json` is not needed to build. The
> Xcode build runs with signing disabled.

### Run the same checks locally
```sh
# Android / JVM / common
./gradlew testDebugUnitTest :app:lintDebug :app:assembleDebug

# iOS (macOS only)
./gradlew :shared:iosSimulatorArm64Test :shared:linkDebugFrameworkIosSimulatorArm64
xcodebuild build -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 16' \
  CODE_SIGNING_ALLOWED=NO CODE_SIGNING_REQUIRED=NO
```

### Making iOS optional (cost control)
macOS minutes are ~10× Linux. If cost is a concern, you can make the `ios` job
advisory instead of blocking by marking it non-required in the branch protection
settings, or gate it to run only on labeled PRs / release branches.

---

## Phase 2 — Quality gates (done)

Wired via a single `build-logic` convention plugin,
[`QualityConventionPlugin`](../build-logic/convention/src/main/kotlin/QualityConventionPlugin.kt)
(id `nearaid.quality`), applied once in the root `build.gradle.kts`. It configures
**detekt** and **Spotless** across the root and every subproject via
`allprojects {}`, so all modules inherit the rules without editing 16 build scripts.

### detekt (blocking)
- Config: [`config/detekt/detekt.yml`](../config/detekt/detekt.yml) with
  `buildUponDefaultConfig = true`.
- Source: KMP source sets are listed explicitly (`commonMain`, `androidMain`,
  `iosMain`, `main`, plus test sets) — detekt's default detection only covers
  `src/main`.
- **Baselines** (`<module>/detekt-baseline.xml`, committed) grandfather the
  pre-existing issues, so only *new* violations fail the build.
- CI: `./gradlew detekt` in the `quality` job — a required check.

Regenerate baselines after intentionally accepting new debt:
```sh
./gradlew detektBaseline    # rewrites every module's detekt-baseline.xml
```

### Spotless / ktlint (advisory for now)
- Formats `src/**/*.kt` and `*.gradle.kts` with ktlint.
- The existing code predates ktlint formatting, so `spotlessCheck` currently
  fails repo-wide. The CI step is therefore **advisory** (`continue-on-error`).

**To make formatting a required gate:**
1. Run `./gradlew spotlessApply` on a dedicated formatting-only PR (large diff —
   land it separately from feature work to avoid conflicts).
2. Remove the `continue-on-error: true` line from the `Spotless` step in
   `.github/workflows/ci.yml`.

### Not yet included
- **Kover** (coverage report + thresholds) — a natural Phase 2.1 add.

---

## Phase 3 — CD: Android release (done — needs secrets)

Workflow: [`.github/workflows/release.yml`](../.github/workflows/release.yml).
Triggered by pushing a `v*` tag (or manual dispatch). It decodes the keystore,
builds a **signed release APK**, and pushes it to **Firebase App Distribution**
(group `testers`), also uploading the APK as a build artifact.

**Signing** is wired in `app/build.gradle.kts`: it reads `keystore.properties`
(local, gitignored) first, then env vars (CI). If no signing material is present,
the release build stays debug-signed — so nothing breaks for devs without a key.

### One-time setup

**1. Generate a keystore** (do this once; keep the `.jks` safe — losing it means
you can no longer update the app on Play):
```sh
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias nearaid \
  -keyalg RSA -keysize 2048 -validity 10000
```

**2. Local signed builds** (optional): copy `keystore.properties.example` to
`keystore.properties`, fill in the passwords, and put `release.keystore` at the
repo root. Then `./gradlew :app:assembleRelease` produces a signed APK.

**3. Add GitHub Actions secrets** (Repo → Settings → Secrets and variables →
Actions):

| Secret | How to get it |
|--------|---------------|
| `ANDROID_KEYSTORE_BASE64` | `base64 -i release.keystore \| pbcopy` (macOS) |
| `ANDROID_KEYSTORE_PASSWORD` | the store password you chose |
| `ANDROID_KEY_ALIAS` | `nearaid` (or your alias) |
| `ANDROID_KEY_PASSWORD` | the key password you chose |
| `FIREBASE_APP_ID` | Firebase Console → Project settings → your Android app (`1:...:android:...`) |
| `FIREBASE_SERVICE_ACCOUNT` | a Google Cloud service-account JSON with the *Firebase App Distribution Admin* role (paste the whole file contents) |

**4. Create the `testers` group** in Firebase Console → App Distribution, and add
testers. Change the `groups:` value in `release.yml` to match your group name(s).

### Release
```sh
git tag v1.0.0 && git push origin v1.0.0
```

> Note: `BASE_URL` / `WS_URL` are hardcoded per build type (`release` →
> `api.nearaid.app`). Inject via secrets/BuildConfig if environments differ.
> To ship an **AAB** to Play later, swap `assembleRelease` → `bundleRelease` and
> add `r0adkll/upload-google-play` with a Play service-account secret.

---

## Phase 4 — CD: iOS release (roadmap)

Most greenfield — requires setup before automation is possible:
1. Set `DEVELOPMENT_TEAM`, switch to manual signing, add an `ExportOptions.plist`
   (none exist today).
2. **Fastlane `match`** for certs/profiles, with an App Store Connect API key.
3. macOS workflow: build `Shared` framework → `xcodebuild archive` →
   `exportArchive` → **`pilot`/`deliver`** to TestFlight.

Required secrets (suggested names): `APP_STORE_CONNECT_API_KEY_ID`,
`APP_STORE_CONNECT_API_ISSUER_ID`, `APP_STORE_CONNECT_API_KEY`, `MATCH_PASSWORD`,
`MATCH_GIT_URL`.

---

## Secrets & signing — reference

Files that are **gitignored** and must be provisioned in CI when the relevant
phase lands (currently only needed from Phase 3 onward):

- `*.jks` / `*.keystore` / `keystore.properties` — Android signing
- `google-services.json` — only if the `google-services` plugin is later applied
- iOS certificates / provisioning profiles — via Fastlane `match`

Store each as an encrypted GitHub Actions secret; never commit them.

---

## Recommended branch protection

Once green, require these checks to merge into `main`:
- `Android / JVM / common` (required)
- `Static analysis` (required — detekt gates; Spotless advisory until the
  formatting PR lands)
- `iOS (shared + Xcode)` (required, or advisory if controlling cost)
