# Navigation

NearAid uses **type-safe Navigation Compose** — routes are `@Serializable` `data object` /
`data class` markers, and each feature registers its own composables against them via
`NavGraphBuilder` extensions, so **no feature depends on another feature**.

## Route registry (`:core:navigation`)

`core/navigation/Routes.kt` is the single source of truth (the only file in the module):

| Group | Routes |
|---|---|
| Graphs | `AuthGraph`, `MainGraph` |
| Auth | `WelcomeRoute`, `PhoneRoute`, `OtpRoute(requestId, phone)`, `ProfileSetupRoute` |
| Tabs | `HomeRoute`, `ActivityRoute`, `MessagesRoute`, `ProfileRoute` |
| Pushed | `ListingDetailRoute(listingId)`, `ChatRoute(claimId, threadId, title)`, `NotificationsRoute`, `PublicProfileRoute(userId)`, `VerificationRoute`, `SettingsRoute`, `PostChooserRoute`, `CreateListingRoute(type)` |

Arguments travel in the route object and are read type-safely with `entry.toRoute<Route>()` —
no string keys, no manual arg parsing.

## Two-level host

```
NearAidNavHost(startLoggedIn)          app/navigation/NearAidNavHost.kt
├── authGraph(...)          → AuthGraph      (Welcome → Phone → Otp → ProfileSetup)
└── composable<MainGraph> → MainScreen(...)  app/navigation/MainScreen.kt
        └── nested NavHost (start = HomeRoute)
            ├── discoveryGraph   Home · ListingDetail · Notifications
            ├── activityGraph    Activity
            ├── messagesGraph    Messages · Chat
            ├── postGraph        PostChooser · CreateListing
            └── profileGraph     Profile · PublicProfile · Verification · Settings
```

- **Root host** picks `startDestination = MainGraph` if already logged in, else `AuthGraph`.
  Authenticating navigates to `MainGraph` with `popUpTo(AuthGraph){ inclusive = true }`;
  logging out does the inverse — **back never crosses the auth boundary**.
- **Main scaffold** (`MainScreen`) holds the nested `NavHost` and the bottom bar.

## Bottom navigation

`app/navigation/TopLevelDestination.kt` — an enum of the four tabs
(`@StringRes labelRes`, `icon`, `route`):

| Tab | Icon | Route |
|---|---|---|
| HOME | `Home` | `HomeRoute` |
| ACTIVITY | `CheckCircle` | `ActivityRoute` |
| MESSAGES | `AutoMirrored.Chat` | `MessagesRoute` |
| PROFILE | `Person` | `ProfileRoute` |

The centre **Post** action is a marigold-gradient FAB (`Role.Button`) that navigates to
`PostChooserRoute` — it's an action, not a tab. Tab switching uses the standard
`popUpTo(findStartDestination){ saveState = true }; launchSingleTop = true; restoreState =
true`, and the selected tab is detected via
`currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) }`.

## Per-feature graphs

Each feature exposes a `NavGraphBuilder` extension consumed by the host:

| Feature | Extension | Destinations |
|---|---|---|
| auth | `authGraph(navController, onAuthenticated)` | nested `navigation<AuthGraph>`, Welcome/Phone/Otp/ProfileSetup |
| discovery | `discoveryGraph(navController)` | Home, ListingDetail, Notifications |
| messages | `messagesGraph(navController)` | Messages, Chat |
| post | `postGraph(navController)` | PostChooser, CreateListing |
| profile | `profileGraph(navController, onLoggedOut)` | Profile, PublicProfile, Verification, Settings |
| activity | `activityGraph(navController)` | Activity |

Because every graph is keyed off the shared `Routes.kt`, cross-feature navigation (e.g.
Messages → a user's PublicProfile) works without a compile-time dependency between the two
feature modules.
