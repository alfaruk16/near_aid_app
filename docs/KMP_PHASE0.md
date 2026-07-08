# KMP Phase 0 — Walking Skeleton (done)

This is the first step of the [KMP migration roadmap](KMP_MIGRATION_ROADMAP.md). It proves the
Kotlin Multiplatform toolchain end-to-end (Gradle KMP plugin → shared Kotlin → iOS framework →
SwiftUI app) **before** the heavy data-layer work in later phases.

Strategic forks locked in (roadmap §6): **logic-only KMP** with **native SwiftUI** on iOS;
target libraries **Koin + Room-KMP + Ktor + SKIE** (introduced in Phases 1–3, not yet present).

## What changed

| Area | Change |
|---|---|
| `build-logic` | New `nearaid.kmp.library` convention plugin (`KmpLibraryConventionPlugin.kt`) — applies `kotlin("multiplatform")` + `com.android.library`, sets up `androidTarget` + `iosX64/iosArm64/iosSimulatorArm64`. |
| `gradle/libs.versions.toml` | Added `kotlin-multiplatform` and `nearaid-kmp-library` plugin aliases. |
| `:core:model` | Converted from a JVM library to a **KMP module**. Sources moved to `src/commonMain/kotlin`; added an `expect`/`actual platform()` probe (`androidMain`/`iosMain`); build script now produces the `Shared` iOS framework. No consumer edits were needed — the Android modules resolve the KMP module's `androidTarget` variant automatically. |
| `iosApp/` | New SwiftUI shell that links the `Shared` framework and displays a shared `AuthTokens` model + `platform()`. |

## Toolchain

- Xcode 26.4, Kotlin 2.0.21, AGP 8.7.3.
- Gradle runs on the Android Studio JBR (see `org.gradle.java.home` in `gradle.properties`).
- The first Kotlin/Native build downloads the konan toolchain (a few minutes); it is cached afterwards.

## Build & verify

### Android (unchanged)
```bash
./gradlew :app:assembleDebug
```

### Shared iOS framework (Kotlin/Native)
```bash
# Compile + link the framework for the simulator target
./gradlew :core:model:linkDebugFrameworkIosSimulatorArm64
# or build every iOS variant + the Android artifact:
./gradlew :core:model:assemble
```

### iOS app
1. Open `iosApp/iosApp.xcodeproj` in Xcode, pick an iOS Simulator, and Run. The app's
   **"Compile Kotlin Framework"** build phase invokes
   `./gradlew :core:model:embedAndSignAppleFrameworkForXcode` automatically, so no manual Gradle
   step is required.
2. Or from the command line:
   ```bash
   xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
     -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build
   ```

The screen should show `Running on: iOS <version>` and the shared `AuthTokens.accessToken`,
proving the shared Kotlin model and `expect`/`actual` cross the boundary into Swift.

## Notes for later phases

- `Shared` is currently produced by `:core:model` alone. As more modules become multiplatform
  (Phase 1+), it becomes the umbrella framework the `iosApp` links against — keep the framework
  `baseName = "Shared"`.
- The `nearaid.kmp.library` convention plugin is reusable: future KMP modules apply it and add
  their own `android { namespace = ... }` (and framework binary only where an iOS artifact is
  exported).
