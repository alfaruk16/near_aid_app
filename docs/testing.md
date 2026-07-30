# Testing

NearAid tests each layer with fast JVM unit tests, guards Compose accessibility under
Robolectric, and reserves hardware-only suites (BLE, benchmarks) for manual/device runs.

## Stack

| Tool | Version | Use |
|---|---|---|
| JUnit 4 | 4.13.2 | test runner |
| MockK | 1.13.13 | mocking (`mockk`, `every`, `coEvery`, `coVerify`) |
| Turbine | 1.2.0 | asserting `Flow`/effect emissions (`.test { }`) |
| kotlinx-coroutines-test | — | `runTest`, `UnconfinedTestDispatcher` |
| Robolectric | 4.14.1 | JVM Compose/a11y tests |
| Compose ui-test-junit4 | — | `createComposeRule` / `createAndroidComposeRule` |
| Macrobenchmark | — | startup timing (device) |

The unit-test deps (junit, coroutines-test, turbine, mockk) are added to every module by the
`nearaid.android.library` convention plugin — no per-module test wiring.

## Patterns

**ViewModel test** = JUnit4 + MockK + Turbine + a test dispatcher. `MainDispatcherRule` (one
per module) swaps `Dispatchers.Main` for an `UnconfinedTestDispatcher` so `viewModelScope` work
runs **eagerly and synchronously** — construct the ViewModel, read `state.value`, or assert
effects with `viewModel.effect.test { ... }`.

```kotlin
@get:Rule val mainDispatcherRule = MainDispatcherRule()

@Test fun loadsOnInit() {
    coEvery { getNearbyListings(any(), any()) } returns DataResult.Success(page)
    assertEquals(expected, viewModel().state.value.listings)
}
```

Use-case and repository tests mock the layer below (repository interface / API + DAO) and assert
the returned `DataResult` and the `@Dispatcher(IO)`-wrapped calls.

## Coverage (JaCoCo)

`build-logic/convention/.../Jacoco.kt` (`configureJacoco()`, applied by the app & library
convention plugins) registers a per-module **`jacocoTestReport`** task (XML + HTML) over
`testDebugUnitTest`. Its exclusion list scopes coverage to the **logic surface** — ViewModels,
repositories, use cases, mappers, interceptors, utilities — by excluding generated Hilt/Dagger,
Room `*_Impl`/`entity`/`dao`, serializer stubs/`dto`, all Compose (`theme/`, `component/`,
`*Screen*`), navigation markers, and Activities/Application/MessagingService. This is also why
the AI module keeps its untestable native glue under `di/` (see
[ai-semantic-search.md](ai-semantic-search.md)).

```bash
./gradlew :core:ai:jacocoTestReport      # one module
./gradlew jacocoTestReport               # all
```

## What's tested where

| Layer | Representative tests |
|---|---|
| common | `MviViewModelTest`, `DataResultTest`, `util/TimeFormatTest` |
| domain | `AuthUseCasesTest`, `ListingUseCasesTest`, `ClaimUseCasesTest`, `ObserveSessionUseCaseTest`, `PhoneNumberTest`, `RankListingsBySimilarityUseCaseTest` |
| data | `ListingRepositoryImplTest`, `ChatRepositoryImplTest`, `ClaimRepositoryImplTest`, `AuthUserPrefsRepositoryTest`, `mapper/MappersTest` |
| network | `AuthInterceptorTest`, `TokenAuthenticatorTest`, `SafeApiCallTest` |
| ai | `HashingTextEmbedderTest`, `MediaPipeTextEmbedderTest`, `CompositeTextEmbedderTest` |
| designsystem | `AccessibilityChecksTest`, `AccessibilityTest` (Robolectric) |
| features | a `*ViewModelTest` per screen (Home, ListingDetail, Chat, Conversations, CreateListing, Otp, Phone, ProfileSetup, Profile, PublicProfile, Settings, Verification, Activity, Notifications) |
| app | `MainViewModelTest`, `navigation/TopLevelDestinationTest` |

## Hardware-only suites (excluded from CI)

- **BLE handoff (`:core:proximity`)** — `BleProximityConfirmer` advertises + scans a
  `HandoffToken` over BLE to resolve `ProximityResult.Confirmed`. Only `HandoffTokenTest`
  (token logic) runs on the JVM; the **two-phone handoff is a manual hardware proof**, not a
  checked-in `connectedAndroidTest`. See [ci-cd.md](ci-cd.md).
- **Startup benchmark (`:benchmark`)** — `StartupBenchmark.kt` (`MacrobenchmarkRule`,
  `StartupTimingMetric`, cold/warm × 5) via `:benchmark:connectedBenchmarkAndroidTest` on a
  device.

## Running

```bash
./gradlew testDebugUnitTest              # all unit tests
./gradlew :feature:discovery:testDebugUnitTest --tests "*HomeViewModelTest"
./gradlew lint                           # shared lint.xml; a11y checks are errors
```

> Local runs need the JBR `JAVA_HOME` (`org.gradle.java.home` in `gradle.properties`); CI uses
> JDK 17 (Temurin). See [ci-cd.md](ci-cd.md).
