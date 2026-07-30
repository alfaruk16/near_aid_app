# Design System & Accessibility

`:core:designsystem` holds every reusable Compose component, the theme, and the accessibility
helpers. Screens compose from these — they don't hand-roll buttons, colors, or a11y semantics.

## Components (`component/`)

| File | Components |
|---|---|
| `Buttons.kt` | `NearAidButton(text, onClick, variant, enabled, loading, leadingIcon)`; `NearAidButtonVariant { Primary, Teal, Ink, Rust, Ghost }` — 50 dp min height, loading swaps in a spinner |
| `Common.kt` | `SectionLabel`, `NearAidChip` (`Role.Tab`, 48 dp), `Avatar`, `NearAidTopBar`, `EmptyState`, `NearAidSegmentedTabs` (`selectableGroup()` + `Role.Tab`), `TextChooserRow` |
| `Badges.kt` | `VerifiedBadge`, `TrustScore`, `UrgencyTag`, `TagChip`, `StatusPill(status)` |
| `TextField.kt` | `NearAidTextField(...)` over `OutlinedTextField`; sets `error` semantics; optional `leadingIcon` |
| `ListingCard.kt` | `ListingCardView(...)`, `formatDistance(km)` |
| `CategoryIcon.kt` | `categoryIcon(key)`, `CategoryIconBox(...)` |
| `CollectEffect.kt` | `CollectEffect(effects, onEffect)` — lifecycle-aware one-shot MVI effect collector (`STARTED`-gated so effects never replay) |
| `Accessibility.kt` | a11y `Modifier` helpers (below) |

## Theming (`theme/`)

A **semantic color system** layered over Material 3, mirroring Material's function/object
duality:

- `NearAidTheme(darkTheme = isSystemInDarkTheme()) { ... }` — the composable. Provides
  `LocalNearAidColors` + `LocalIsDarkTheme`, then wraps `MaterialTheme(colorScheme,
  NearAidTypography, NearAidShapes)`.
- `object NearAidTheme { val colors: NearAidColors @Composable get() = LocalNearAidColors.current }`
  — the read side. **Components always read `NearAidTheme.colors.<token>`, never raw hex.**

`Color.kt` defines `@Immutable data class NearAidColors` with semantic tokens — `ink/ink2/ink3`
(text), `paper/surface/stage` (backgrounds), `line/line2` (borders), and accent families
`marigold*`, `teal*`, `rust*`, `blueAccent*`, plus `onMarigold`. Two instances,
`LightNearAidColors` / `DarkNearAidColors` (dark uses warm-neutral surfaces, not pure grey).

Category and urgency accents are theme-aware too: `CategoryColors`/`UrgencyColors` expose light
and `*Dark` variants, selected via `categoryAccentFor(key)` / `urgencyAccentFor(urgency)` which
read `LocalIsDarkTheme`.

`Type.kt` (`NearAidTypography`) and `Shapes` (rounded 10–26 dp) round out the theme. Fonts
currently fall back to `FontFamily.Default`; the spec pairs Bricolage Grotesque (display), Plus
Jakarta Sans (body), and Hind Siliguri (Bangla).

## Accessibility

**Helpers — `component/Accessibility.kt`** (`Modifier` extensions):

| Modifier | Effect |
|---|---|
| `headingSemantics()` | marks a `heading()` (section labels, top-bar titles) |
| `politeLiveRegion()` | `LiveRegionMode.Polite` (empty states) |
| `accessibleClickable(onClickLabel, role, onClick)` | labeled `clickable` + 48×48 dp min |
| `statusSemantics(description)` | `contentDescription` + polite live region for silent status nodes (e.g. progress spinners) |

**Guardrails:** 48 dp minimum touch targets (`accessibleClickable`, `NearAidChip`,
`NearAidSegmentedTabs`), 50 dp buttons; `Role.Tab` for chips/tabs/bottom-nav, `Role.Button` for
the FAB; headings and polite live regions on the relevant nodes.

**Automated checks (Robolectric, JVM — `core/designsystem/src/test/`):**

- `AccessibilityChecksTest.kt` — renders a `Sample()` of all interactive components and asserts,
  over `onAllNodes(hasClickAction())`, that **every clickable node is labeled** (contentDescription
  / text / stateDescription) **and ≥ 48 dp** in both dimensions.
- `AccessibilityTest.kt` — asserts `Role.Tab`, selected state, and touch-target size on
  `NearAidChip` / `NearAidSegmentedTabs`.

These run in CI as ordinary unit tests, so an a11y regression fails the build.

**Lint (`lint.xml`, shared via the build-logic Kotlin/Android convention):** the a11y checks
`ContentDescription`, `RedundantContentDescription`, `ClickableViewAccessibility`, `LabelFor`,
`KeyboardInaccessibleWidget` are promoted to **`error`**. (Android lint's a11y is
View/XML-oriented; Compose semantics are covered by the tests above.)

See [testing.md](testing.md) for the wider test strategy.
