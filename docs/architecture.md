# Architecture

NearAid is a **multi-module** Android app built on **Clean Architecture + MVI**, with Gradle
**convention plugins** (`build-logic/`) keeping every module's build script to a few lines.

## Layers & dependency direction

```
:feature:*  ──►  :core:domain  ◄──  :core:data  ──►  :core:network / :core:database / :core:datastore
   (UI/MVI)      (interfaces +          (impls +
                  use cases)             mappers)
```

- **Features depend only on `:core:domain`** (use cases + repository *interfaces*) — never on
  `:core:data`. The dependency rule points inward: UI → domain ← data.
- `:core:domain` is pure app logic: repository interfaces + `@Inject`-constructor use cases.
- `:core:data` implements those interfaces and maps DTOs/entities ↔ domain models.

| Module | Role |
|---|---|
| `:core:common` | MVI base, `DataResult`/`AppError`, dispatcher qualifiers, time utils |
| `:core:model` | domain data classes + enums (no Android deps) |
| `:core:domain` | repository interfaces, use cases, `ObserveSessionUseCase`, `TextEmbedder` |
| `:core:data` | repository impls, DTO→domain & domain↔entity mappers |
| `:core:network` | Retrofit/OkHttp, API services, DTOs, interceptors, `ChatSocket` |
| `:core:database` | Room DB, cache entities & DAOs |
| `:core:datastore` | Preferences DataStore (auth tokens, language, search radius) |
| `:core:designsystem` | Compose components, theme, accessibility helpers |
| `:core:navigation` | type-safe route registry |
| `:core:proximity` | BLE handoff confirmation (`ProximityConfirmer`) |
| `:core:ai` | on-device embedders — see [`ai-semantic-search.md`](ai-semantic-search.md) |
| `:feature:{auth,discovery,post,activity,messages,profile}` | screens + ViewModels + nav graphs |
| `:app` | `NearAidApplication`, `MainActivity`, root nav host, build config, FCM |
| `:benchmark` | Macrobenchmark startup timing |

## MVI (`:core:common/mvi`)

Three marker interfaces — `UiState`, `UiIntent`, `UiEffect` (`MviContract.kt`) — and one base
class, `MviViewModel<State, Intent, Effect>` (`MviViewModel.kt`):

| Member | Purpose |
|---|---|
| `state: StateFlow<State>` | backed by a `MutableStateFlow(initialState())` |
| `effect: Flow<Effect>` | one-shot, backed by a `Channel(BUFFERED).receiveAsFlow()` — never replays |
| `onIntent(intent)` | abstract reducer entry point |
| `setState { copy(...) }` | functional state update (`protected`) |
| `sendEffect(effect)` | emit a one-shot effect on `viewModelScope` (`protected`) |
| `currentState` | snapshot of `state.value` (`protected`) |

Each screen has a `*Contract.kt` (State/Intent/Effect), a `*ViewModel.kt` (`@HiltViewModel`
extending `MviViewModel`), and a `*Screen.kt` composable. Effects reach the UI via
`CollectEffect(viewModel.effect) { ... }` (lifecycle-aware, `STARTED`-gated). ~14 ViewModels
follow this shape (e.g. `HomeViewModel : MviViewModel<HomeState, HomeIntent, HomeEffect>`).

## Result & error model (`:core:common/result`)

```kotlin
sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Failure(val error: AppError) : DataResult<Nothing>
}
```

Repositories and use cases return `DataResult` so the UI never sees exceptions or HTTP types.
Helpers: `map`, `onSuccess`, `onFailure`, `getOrNull`. `AppError` is a sealed class mapping
transport/HTTP failures to intent: `Network`, `Unauthorized` (401), `Forbidden` (403),
`NotFound` (404), `Validation(message, fieldErrors)` (400/422), `Conflict` (409),
`RateLimited` (429), `Server(code, message)` (5xx), `Unknown`.

## Threading (`:core:common/dispatcher`)

Dispatchers are injected, never referenced directly, so they can be swapped in tests:

```kotlin
@Qualifier annotation class Dispatcher(val dispatcher: NearAidDispatcher)
enum class NearAidDispatcher { Default, IO, Main }
```

`DispatchersModule` provides all three (`Dispatchers.IO/Default/Main`). Repositories run on
`@Dispatcher(NearAidDispatcher.IO)`; the AI embedder uses `Default`.

## Dependency injection (Hilt)

`@HiltAndroidApp NearAidApplication` is the graph root; all modules install into
`SingletonComponent`. Two idioms:

- **`@Binds`** for interface→impl — e.g. `DataModule` binds the 9 repository impls; `AiModule`
  binds the embedders.
- **`@Provides`** (`object` modules) for constructed values — `NetworkModule` (Retrofit, OkHttp,
  APIs), `DatabaseModule` (Room), `DataStoreModule`, `DispatchersModule`, and `app/di/AppModule`
  (the `@BaseUrl`/`@WsUrl` strings from `BuildConfig`).

Feature ViewModels are `@HiltViewModel`; screens obtain them with `hiltViewModel()`.

## Build logic (`build-logic/convention`)

Every module applies one or more convention plugins instead of repeating config. IDs
registered in `build-logic/convention/build.gradle.kts` (group `com.nearaid.buildlogic`):

| Plugin id | Configures |
|---|---|
| `nearaid.android.application` | app module; SDK/version, test runner, JaCoCo |
| `nearaid.android.application.compose` / `.library.compose` | Compose (BOM, material3, icons, tooling) |
| `nearaid.android.library` | library module; Kotlin, JaCoCo, unit-test deps (junit, coroutines-test, turbine, mockk) |
| `nearaid.android.feature` | library + compose + hilt **and** wires the 5 core deps every feature needs |
| `nearaid.android.hilt` | KSP + Hilt |
| `nearaid.android.room` | KSP + Room (schema export) |
| `nearaid.jvm.library` | plain Kotlin/JVM |

`AndroidFeatureConventionPlugin` auto-adds `:core:{common,model,domain,designsystem,navigation}`
plus `androidx-core-ktx`, lifecycle, navigation-compose, hilt-navigation-compose,
serialization, and Coil — so a feature's `build.gradle.kts` is just the plugin alias + a
namespace. SDK/Java levels are centralized in `NearAidBuildConfig.kt`; shared lint config in
`lint.xml` (see [design system & accessibility](design-system-and-accessibility.md)).

## See also

- [Data, networking, auth & realtime](data-and-networking.md)
- [Navigation](navigation.md)
- [Design system & accessibility](design-system-and-accessibility.md)
- [Testing](testing.md) · [CI/CD](ci-cd.md) · [AI semantic search](ai-semantic-search.md)
