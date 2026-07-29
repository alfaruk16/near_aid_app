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

Builds `assembleRelease` and publishes a GitHub Release with auto-generated
notes and the APK attached.

> The release APK is **unsigned** — there is no signing config in the repo. To
> ship a signed build, add a `signingConfig` in `app/build.gradle.kts` fed by
> repository secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`,
> `KEY_PASSWORD`), decode the keystore in a workflow step, and upload
> `app-release.apk` instead of `app-release-unsigned.apk`.

## Not run in CI

- **Instrumented / `connectedAndroidTest`** and the **BLE proximity hardware
  proof** (`:core:proximity` `BleProximityHardwareTest`) — these need real
  Bluetooth radios (two phones for the end-to-end handoff), which hosted
  runners don't provide. Run them locally per `scripts/ble-proximity-proof.sh`.
- **Macrobenchmark** (`:benchmark`) — needs a physical device / configured
  emulator; run locally.
- **Firebase** — the `google-services` Gradle plugin is not applied, so no
  `google-services.json` secret is required to build.
