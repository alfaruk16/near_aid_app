# CI/CD

This document describes the CI/CD setup for **NearAid** (Kotlin Multiplatform:
Android `:app` + iOS `iosApp`/`:shared`) and the roadmap for extending it.

## Status at a glance

| Phase | Scope | State |
|-------|-------|-------|
| 0 | Make the build CI-portable | ✅ Done |
| 1 | PR validation (build + test + lint) | ✅ Done (`.github/workflows/ci.yml`) |
| 2 | Quality gates (detekt / spotless / Kover) | ⬜ Not started |
| 3 | CD — Android release (signing → Play/Firebase) | ⬜ Not started |
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

## Phase 2 — Quality gates (roadmap)

None configured yet. Recommended, wired via `build-logic` convention plugins so
all 16 modules inherit them:

- **detekt** (static analysis) — introduce with a baseline to avoid a wall of
  initial violations.
- **spotless** + **ktlint** (formatting) — `kotlin.code.style=official` is set.
- **Kover** (coverage) — report + optional thresholds.

Add each as a step in the `android` job and mark it a required check.

---

## Phase 3 — CD: Android release (roadmap)

Prerequisites (these do not exist yet):
1. **Signing config** in `app/build.gradle.kts` — the `release` build type is
   currently debug-signed. Read keystore path + passwords from env /
   `keystore.properties`; store the keystore as a base64 GitHub secret and decode
   it in the workflow.
2. **`BASE_URL` / `WS_URL`** are hardcoded per build type today. Inject via
   secrets/BuildConfig if release environments differ.
3. Publishing target:
   - **Firebase App Distribution** — lightest first step for internal testers.
   - **Play Store** — Fastlane `supply` or `r0adkll/upload-google-play` with a
     Play service-account JSON secret.

Workflow shape: trigger on tag/release → `./gradlew :app:bundleRelease` → sign →
upload → publish.

Required secrets (suggested names): `ANDROID_KEYSTORE_BASE64`,
`ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`,
`PLAY_SERVICE_ACCOUNT_JSON` (and/or `FIREBASE_APP_ID` + `FIREBASE_TOKEN`).

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
- `iOS (shared + Xcode)` (required, or advisory if controlling cost)
- Phase 2 quality gates once added.
