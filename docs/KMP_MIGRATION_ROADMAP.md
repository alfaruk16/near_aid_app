# NearAid — Kotlin Multiplatform (KMP) Migration Roadmap

Goal: share NearAid's business logic — and optionally the UI — between **Android** and **iOS**
using Kotlin Multiplatform, with the **smallest, lowest-risk sequence** given the current
architecture.

The good news: NearAid is already structured for this. It's Clean Architecture + MVI with a
strict inward dependency rule, `kotlinx.serialization` + Coroutines/Flow throughout (both
multiplatform), and pure-Kotlin `:core:model` / `:core:domain`. The migration is mostly
**library swaps and Gradle restructuring**, not rewrites.

---

## 1. Strategy: share inside-out

Migrate **bottom-up along the dependency rule** — inner, purest layers first. Each phase ships a
working Android app; iOS comes online once the shared foundation is stable.

```
model → domain → common(MVI) → network → datastore → database → data → [presentation] → [UI]
 pure    pure      lifecycle     Ktor      DataStore   Room-KMP   glue     ViewModels    Compose MP
 ✅       ✅(-DI)    swap          swap      swap        swap                swap          decision
```

Two strategic forks decide the scope (see §6):
- **Logic-only KMP** (share down to ViewModels; native SwiftUI on iOS) — lower risk, idiomatic iOS.
- **Full Compose Multiplatform** (share the UI too) — maximum reuse, one codebase.

Recommended: **start logic-only**, keep Compose MP as a fast-follow once the shared core is proven.

---

## 2. Shareability assessment (per current module)

| Module | Today | Target source set | Effort | What changes |
|---|---|---|---|---|
| `:core:model` | pure Kotlin | `commonMain` | 🟢 Low | Move as-is. |
| `:core:domain` | pure Kotlin + `@Inject` | `commonMain` | 🟢 Low | Drop `javax.inject`; wire via Koin. |
| `:core:common` | MVI base, `DataResult`, `TimeFormat`, `@Dispatcher` | `commonMain` | 🟡 Med | `ViewModel`→KMP lifecycle; `Dispatchers.IO`→`expect`/Dispatchers default. |
| `:core:navigation` | `@Serializable` routes | `commonMain` | 🟢 Low | Serialization is already MP. |
| `:core:network` | Retrofit + OkHttp + interceptors | `commonMain` + engines | 🔴 High | **Retrofit→Ktor client**; interceptor/authenticator→Ktor plugins; OkHttp WS→Ktor WS. |
| `:core:datastore` | AndroidX DataStore | `commonMain` + `expect` paths | 🟡 Med | DataStore-KMP; token storage→platform-secure (§5). |
| `:core:database` | Room + DAOs | `commonMain` | 🟡 Med | **Room-KMP** (bundled SQLite driver) or SQLDelight. |
| `:core:data` | RepositoryImpl + mappers | `commonMain` | 🟡 Med | Mappers move as-is; repos depend on the swapped network/db. |
| `:core:designsystem` | Compose M3 + theme | `commonMain` (CMP) *or* stays Android | 🟡/🔴 | Only if Full-CMP; Coil→Coil3. |
| `:feature:*` ViewModels + Contracts | MVI | `commonMain` | 🟡 Med | Share once `:core:common` + DI are MP. |
| `:feature:*` Screens (Compose) | Compose | `commonMain` (CMP) *or* per-platform | 🔴 High | Only if Full-CMP. |
| `:app` | Hilt host, NavHost, FCM | Android only | 🔴 High | Becomes the Android target; new `iosApp` alongside. |

---

## 3. Library migration map

| Concern | Current (Android) | KMP replacement | Notes |
|---|---|---|---|
| DI | **Hilt** | **Koin** (or kotlin-inject-anvil) | Hilt is Android/JVM-only. Biggest cross-cutting change — touches every module. |
| HTTP | **Retrofit + OkHttp** | **Ktor Client 3.x** | Engines: OkHttp (Android) / Darwin (iOS) via `expect`. `ContentNegotiation` + kotlinx.serialization. |
| Interceptors | `AuthInterceptor`, `TokenAuthenticator` | Ktor `Auth`/`plugin` + `HttpRequestRetry` | Bearer + 401-refresh becomes a Ktor `Auth` provider with `refreshTokens`. |
| WebSocket | OkHttp `WebSocket` (`ChatSocket`) | Ktor `WebSockets` plugin | Same Flow-of-`ChatMessage` surface. |
| JSON | kotlinx.serialization | *(unchanged)* ✅ | Already multiplatform. |
| DB | **Room** | **Room-KMP** (2.7+) or **SQLDelight** | Room-KMP keeps DAOs/entities nearly verbatim → lower churn. SQLDelight = SQL-first, more idiomatic MP. |
| Key-value | AndroidX **DataStore** | **DataStore-KMP** (1.1+) or **multiplatform-settings** | Prefs (language/radius) trivially portable. |
| Token storage | DataStore (plain) | `expect`/`actual`: **EncryptedSharedPreferences/Keystore** (Android) + **Keychain** (iOS) | Security upgrade worth doing during the move. |
| Async | Coroutines/Flow | *(unchanged)* ✅ | Multiplatform. |
| ViewModel | `androidx.lifecycle.ViewModel` | **lifecycle-viewmodel (KMP, 2.8+)** | `MviViewModel` stays almost identical in `commonMain`. |
| Images | **Coil** | **Coil 3** (KMP) | Only relevant for Full-CMP. |
| Navigation | Navigation-Compose | **Navigation-Compose (CMP)** or Decompose/Voyager | Routes already `@Serializable`. |
| UI | Compose (Android) | **Compose Multiplatform** | Full-CMP only; else native SwiftUI on iOS. |
| Push | FCM | `expect`/`actual`: FCM (Android) + Firebase iOS/APNs | Not shared — thin platform layer. |
| Build | Convention plugins (AGP) | KMP + Compose MP Gradle plugins | Convention plugins get `kotlin("multiplatform")` variants. |

---

## 4. Phased plan

### Phase 0 — Foundations & PoC (de-risk)
- Add the **Kotlin Multiplatform** + (later) **Compose Multiplatform** Gradle plugins to `build-logic`;
  create KMP convention plugins (`nearaid.kmp.library`).
- Stand up an empty `iosApp` (SwiftUI shell) + a shared `:shared` umbrella (or convert an existing core module) as a **walking skeleton**: one `expect fun platform(): String`, called from both apps.
- Decide the two forks in §6 **before** writing much shared code.
- Confirm toolchain: Kotlin 2.x, Compose MP-compatible AGP, Xcode on a Mac/CI runner.

### Phase 1 — Share the pure core
- Convert `:core:model`, `:core:domain`, `:core:navigation` to KMP (`commonMain`).
- Remove `javax.inject` from `:core:domain`; introduce **Koin** modules (start Android-only, same graph Hilt built).
- Convert `:core:common`: `MviViewModel` onto KMP lifecycle-viewmodel; `DataResult`/`AppError`/`TimeFormat` to common; make `@Dispatcher`/`Dispatchers.IO` an `expect` or use `Dispatchers.Default`.
- **Milestone:** Android app runs unchanged on the shared pure core + Koin.

### Phase 2 — Share data access
- **Network:** replace Retrofit services with **Ktor Client** interfaces; port `AuthInterceptor`→Ktor `Auth`, `TokenAuthenticator`→`refreshTokens`, `safeApiCall`→a common wrapper over Ktor responses, `ChatSocket`→Ktor `WebSockets`. Engines via `expect` (OkHttp/Darwin).
- **DB:** migrate `:core:database` to **Room-KMP** (or SQLDelight); DAOs/entities move to common.
- **DataStore:** move prefs to DataStore-KMP; implement `expect`/`actual` **secure token store**.
- **Data:** `:core:data` mappers + `RepositoryImpl`s move to common (they already depend only on interfaces).
- **Milestone:** the entire non-UI stack is in `commonMain`; Android app fully runs on it.

### Phase 3 — Share presentation (ViewModels)
- Move each `feature/*` `*Contract.kt` + `*ViewModel.kt` to `commonMain` (they extend the now-shared `MviViewModel` and inject use cases via Koin).
- Keep Android `Screen.kt` composables as-is, bound to shared ViewModels.
- **Milestone:** iOS can now drive shared ViewModels from SwiftUI (observe `StateFlow` via SKIE/KMP-NativeCoroutines). **This is the logic-only KMP finish line.**

### Phase 4 — Share the UI (optional, Full-CMP)
- Convert `:core:designsystem` to Compose Multiplatform (theme is pure; Coil→Coil3; fonts via CMP resources).
- Move `feature/*` `Screen.kt` to `commonMain`; adopt CMP navigation (or Decompose/Voyager).
- `iosApp` hosts the shared UI via `ComposeUIViewController`.
- **Milestone:** one shared UI on both platforms.

### Phase 5 — Platform edges & polish
- `expect`/`actual` for FCM/APNs push, deep links, share sheet, permissions, secure storage.
- iOS CI (Xcode build + `xcodebuild test`), fastlane/TestFlight.
- Port the Robolectric a11y tests to `commonTest`; add iOS accessibility (VoiceOver) checks.

---

## 5. Platform-specific surface (`expect`/`actual` inventory)

| Concern | Android `actual` | iOS `actual` |
|---|---|---|
| HTTP engine | OkHttp | Darwin |
| SQLite driver | Room Android / bundled | Room native / native driver |
| Secure token store | Keystore + EncryptedSharedPreferences | Keychain |
| Preferences path | `filesDir` | `NSDocumentDirectory` |
| Dispatchers.IO | `Dispatchers.IO` | `Dispatchers.Default`/`IO` |
| Push | FCM service | APNs + Firebase iOS SDK |
| Locale/strings | `stringResource` (or MOKO/CMP resources) | same shared mechanism |
| Image loading | Coil3 Android | Coil3 iOS |

> **Localization payoff:** because all UI copy is already in `strings.xml`, moving to a
> multiplatform resource mechanism (Compose MP `Res` or MOKO resources) is a mechanical lift, not a hunt.

---

## 6. Key decisions (resolve in Phase 0)

1. **iOS UI: Compose Multiplatform vs native SwiftUI.**
   CMP = maximum reuse, one team; SwiftUI = idiomatic iOS feel, but re-implement screens.
   *Recommendation:* logic-only first (SwiftUI), evaluate CMP after the shared core proves out.
2. **Room-KMP vs SQLDelight.**
   Room-KMP = least churn (keep DAOs); SQLDelight = more idiomatic MP + compile-time SQL.
   *Recommendation:* Room-KMP to minimize migration risk.
3. **DI: Koin vs kotlin-inject.**
   Koin = pragmatic, huge ecosystem, runtime; kotlin-inject = compile-time safety.
   *Recommendation:* Koin (simplest Hilt replacement).
4. **iOS coroutine interop:** adopt **SKIE** or **KMP-NativeCoroutines** so Swift sees `Flow`/suspend ergonomically.
5. **Module granularity:** keep the current fine-grained modules (they map cleanly to source sets) rather than collapsing into one `:shared`.

---

## 7. Effort & sequencing

| Phase | Scope | Relative effort | Ships |
|---|---|---|---|
| 0 | Plugins + PoC + decisions | S | walking skeleton |
| 1 | Pure core + Koin | M | Android on shared core |
| 2 | Network + DB + DataStore + data | **L** (Ktor + Room-KMP are the heavy items) | shared non-UI stack |
| 3 | ViewModels shared | M | **logic-only KMP done** |
| 4 | Compose MP UI | **L** | shared UI (optional) |
| 5 | Platform edges + CI | M | iOS release-ready |

**Critical path & biggest risks:** Retrofit→Ktor (auth refresh + WebSocket parity) and Room→Room-KMP.
De-risk both with a spike in Phase 0/early Phase 2 before committing the full data layer.

## 8. What's already in your favor
- Clean Architecture + strict dependency rule → source-set boundaries are already drawn.
- `kotlinx.serialization`, Coroutines/Flow, `@Serializable` routes → already multiplatform.
- MVI with a single small base class → ViewModels port with minimal change.
- Errors-as-values (`DataResult`/`AppError`) → no exception/framework leakage to bridge.
- Convention-plugin Gradle setup → add KMP variants in one place, not per-module.
- All UI strings externalized → i18n is portable from day one.

## 9. Suggested first PR
Phase 0 walking skeleton: KMP Gradle plugin in `build-logic`, convert `:core:model` to `commonMain`,
add an `iosApp` SwiftUI shell that reads one shared model — proving the toolchain end-to-end before
touching the data layer.
