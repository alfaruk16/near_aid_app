# CI/CD

GitHub Actions pipelines for NearAid. Both live under `.github/workflows/`.

## `ci.yml` — build, test, coverage, lint

**Triggers:** every push to `main`/`develop` and every pull request targeting them.
Superseded runs on the same ref are auto-cancelled.

| Job | What it runs | Artifacts |
|---|---|---|
| **build-test** | `assembleDebug` → `testDebugUnitTest` → `jacocoTestReport` | debug APK, unit-test HTML reports, JaCoCo reports |
| **lint** | `./gradlew lint` (shared `lint.xml`; a11y checks are errors) | lint HTML/XML reports |

Toolchain (matches `main`): **JDK 17 (Temurin)**, Gradle **8.11.1** wrapper,
Kotlin **2.0.21**, AGP **8.7.3**. Gradle setup, dependency/build caching, and
wrapper-checksum validation are handled by `gradle/actions/setup-gradle`.

## `release.yml` — tagged release

**Trigger:** pushing a semver tag.

```bash
git tag v1.0.0
git push origin v1.0.0
```

The job builds a **signed** `assembleRelease` (APK) + `bundleRelease` (AAB), publishes a
GitHub Release with auto-generated notes and the APK, and — when a Play service account is
configured — pushes the AAB to Google Play's `internal` track.

### Signing

`app/build.gradle.kts` creates a `release` signing config **only when a keystore is provided
via env vars**; otherwise the release build is unsigned but still succeeds (local dev, PR CI).
The workflow decodes the keystore from a secret and passes the env through to Gradle:

| Secret | Purpose |
|---|---|
| `KEYSTORE_BASE64` | base64 of the upload keystore (`.jks`) |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | key alias |
| `KEY_PASSWORD` | key password |

```bash
base64 -i upload.jks | pbcopy        # → paste into the KEYSTORE_BASE64 secret
```

Env → Gradle mapping: `KEYSTORE_FILE` (path to the decoded keystore), `KEYSTORE_PASSWORD`,
`KEY_ALIAS`, `KEY_PASSWORD`. If `KEYSTORE_BASE64` is unset the workflow logs a warning and
produces `app-release-unsigned.apk`.

### Play publishing (optional)

The **Publish to Google Play** step (`r0adkll/upload-google-play`) uploads the AAB to the
`internal` track. It is **skipped unless** `PLAY_SERVICE_ACCOUNT_JSON` is set, and requires the
app to already exist in the Play Console.

| Secret | Purpose |
|---|---|
| `PLAY_SERVICE_ACCOUNT_JSON` | Play Console service-account JSON key with the "release apps" permission |

Change `track: internal` to `alpha`/`beta`/`production` (and add a rollout `userFraction`) to
promote further. The `mapping.txt` is uploaded alongside for crash deobfuscation (R8 is on for
release builds).

## Not run in CI

- **Instrumented / `connectedAndroidTest`** and the **BLE proximity hardware
  proof** (`:core:proximity` `BleProximityHardwareTest`) — these need real
  Bluetooth radios (two phones for the end-to-end handoff), which hosted
  runners don't provide. Run them locally per `scripts/ble-proximity-proof.sh`.
- **Macrobenchmark** (`:benchmark`) — needs a physical device / configured
  emulator; run locally.
- **Firebase** — the `google-services` plugin is applied, so `app/google-services.json`
  is required at build time. It is git-ignored, so CI injects it from a base64
  repository secret named **`GOOGLE_SERVICES_JSON`**. Create/update it with:

  ```bash
  base64 -i app/google-services.json | pbcopy   # then paste into the secret
  ```

  Missing/empty secret → the build fails at `processDebugGoogleServices`.
