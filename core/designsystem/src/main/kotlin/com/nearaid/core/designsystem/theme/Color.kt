package com.nearaid.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.nearaid.core.model.Urgency

// ---- Brand palette — LIGHT (mirrors the :root design tokens in the UI spec) ----
val Ink = Color(0xFF22202B)
val Ink2 = Color(0xFF56525F)
val Ink3 = Color(0xFF8A8693)

val Paper = Color(0xFFF8F3EA)
val Surface = Color(0xFFFFFFFF)
val Line = Color(0xFFEDE6D8)
val Line2 = Color(0xFFF2ECE0)

val Marigold = Color(0xFFF2A024)
val MarigoldDeep = Color(0xFFDB8908)
val MarigoldTint = Color(0xFFFDEBCB)
val MarigoldSoft = Color(0xFFFDF4E2)

val Teal = Color(0xFF1F7A68)
val TealTint = Color(0xFFDBEDE8)
val TealSoft = Color(0xFFEAF5F2)

val Rust = Color(0xFFC4502E)
val RustTint = Color(0xFFF7E2D9)

val BlueAccent = Color(0xFF3A6EA5)
val BlueTint = Color(0xFFE1ECF6)

val Stage = Color(0xFF2A2333)

val OnMarigold = Color(0xFF3A2400)

// ---- Brand palette — DARK ----
// Warm-neutral dark surfaces (not pure grey) to keep the brand's paper-like feel.
val InkDark = Color(0xFFF3EFE6)        // primary text/foreground on dark
val Ink2Dark = Color(0xFFB7B2BF)       // secondary text
val Ink3Dark = Color(0xFF948FA0)       // tertiary text / hints

val PaperDark = Color(0xFF141319)      // app background
val SurfaceDark = Color(0xFF201E28)    // cards / raised surfaces
val LineDark = Color(0xFF34313E)       // borders
val Line2Dark = Color(0xFF2A2833)      // subtle fills

val MarigoldDark = Color(0xFFF4A835)   // primary accent (brand pops on dark)
val MarigoldDeepDark = Color(0xFFF4B457) // marigold used as text/emphasis on dark
val MarigoldTintDark = Color(0xFF3A2E1A) // marigold container on dark
val MarigoldSoftDark = Color(0xFF2A2318)

val TealDark = Color(0xFF5FC0AC)       // secondary accent, lightened for dark contrast
val TealTintDark = Color(0xFF163832)
val TealSoftDark = Color(0xFF14302B)

val RustDark = Color(0xFFE0805F)       // error, lightened for dark
val RustTintDark = Color(0xFF3E241C)

val BlueAccentDark = Color(0xFF7FA8D4)
val BlueTintDark = Color(0xFF22303F)

val StageDark = Color(0xFF201E28)

// onMarigold stays dark — Marigold is bright enough in both themes for dark text on it.

/**
 * Semantic color set for NearAid. Components read these via [NearAidTheme.colors] so the
 * whole design system reacts to light/dark, instead of referencing static tokens directly.
 */
@Immutable
data class NearAidColors(
    val ink: Color,
    val ink2: Color,
    val ink3: Color,
    val paper: Color,
    val surface: Color,
    val line: Color,
    val line2: Color,
    val marigold: Color,
    val marigoldDeep: Color,
    val marigoldTint: Color,
    val marigoldSoft: Color,
    val teal: Color,
    val tealTint: Color,
    val tealSoft: Color,
    val rust: Color,
    val rustTint: Color,
    val blueAccent: Color,
    val blueTint: Color,
    val stage: Color,
    val onMarigold: Color,
)

val LightNearAidColors = NearAidColors(
    ink = Ink,
    ink2 = Ink2,
    ink3 = Ink3,
    paper = Paper,
    surface = Surface,
    line = Line,
    line2 = Line2,
    marigold = Marigold,
    marigoldDeep = MarigoldDeep,
    marigoldTint = MarigoldTint,
    marigoldSoft = MarigoldSoft,
    teal = Teal,
    tealTint = TealTint,
    tealSoft = TealSoft,
    rust = Rust,
    rustTint = RustTint,
    blueAccent = BlueAccent,
    blueTint = BlueTint,
    stage = Stage,
    onMarigold = OnMarigold,
)

val DarkNearAidColors = NearAidColors(
    ink = InkDark,
    ink2 = Ink2Dark,
    ink3 = Ink3Dark,
    paper = PaperDark,
    surface = SurfaceDark,
    line = LineDark,
    line2 = Line2Dark,
    marigold = MarigoldDark,
    marigoldDeep = MarigoldDeepDark,
    marigoldTint = MarigoldTintDark,
    marigoldSoft = MarigoldSoftDark,
    teal = TealDark,
    tealTint = TealTintDark,
    tealSoft = TealSoftDark,
    rust = RustDark,
    rustTint = RustTintDark,
    blueAccent = BlueAccentDark,
    blueTint = BlueTintDark,
    stage = StageDark,
    onMarigold = OnMarigold,
)

val LocalNearAidColors = staticCompositionLocalOf { LightNearAidColors }

// ---- Category accents (.c-* in the spec) ----
data class CategoryAccent(val container: Color, val content: Color)

object CategoryColors {
    // Light: pale container + saturated content.
    val Food = CategoryAccent(MarigoldTint, MarigoldDeep)
    val Clothes = CategoryAccent(BlueTint, BlueAccent)
    val Medicine = CategoryAccent(RustTint, Rust)
    val Goods = CategoryAccent(TealTint, Teal)
    val Shelter = CategoryAccent(Color(0xFFEAE4F3), Color(0xFF6A4DA3))
    val Other = CategoryAccent(Color(0xFFEEEEEE), Color(0xFF777777))

    // Dark: muted deep container + bright content (each pair ≥4.5:1).
    val FoodDark = CategoryAccent(Color(0xFF3A2E1A), Color(0xFFF4B457))
    val ClothesDark = CategoryAccent(Color(0xFF1E2A38), Color(0xFF7FA8D4))
    val MedicineDark = CategoryAccent(Color(0xFF3A241C), Color(0xFFE0805F))
    val GoodsDark = CategoryAccent(Color(0xFF16302B), Color(0xFF5FC0AC))
    val ShelterDark = CategoryAccent(Color(0xFF241E33), Color(0xFFB9A5E0))
    val OtherDark = CategoryAccent(Color(0xFF2A2833), Color(0xFFA8A2B0))

    fun forKey(key: String?): CategoryAccent = when (key) {
        "food" -> Food
        "clothes" -> Clothes
        "medicine" -> Medicine
        "goods" -> Goods
        "shelter" -> Shelter
        else -> Other
    }

    fun forKeyDark(key: String?): CategoryAccent = when (key) {
        "food" -> FoodDark
        "clothes" -> ClothesDark
        "medicine" -> MedicineDark
        "goods" -> GoodsDark
        "shelter" -> ShelterDark
        else -> OtherDark
    }
}

// ---- Urgency tags (.t-* in the spec) ----
data class UrgencyAccent(val container: Color, val content: Color)

object UrgencyColors {
    val LowContainer = Color(0xFFEAEFEA)
    val LowContent = Color(0xFF5C7A5C)
    val MediumContainer = MarigoldTint
    val MediumContent = MarigoldDeep
    val HighContainer = Color(0xFFFBE4D2)
    val HighContent = Color(0xFFB8651F)
    val CriticalContainer = RustTint
    val CriticalContent = Rust

    val Low = UrgencyAccent(LowContainer, LowContent)
    val Medium = UrgencyAccent(MediumContainer, MediumContent)
    val High = UrgencyAccent(HighContainer, HighContent)
    val Critical = UrgencyAccent(CriticalContainer, CriticalContent)

    // Dark variants (container deep-muted, content bright, ≥4.5:1).
    val LowDark = UrgencyAccent(Color(0xFF1C2A1C), Color(0xFF8FBF8F))
    val MediumDark = UrgencyAccent(Color(0xFF3A2E1A), Color(0xFFF4B457))
    val HighDark = UrgencyAccent(Color(0xFF3A2A1A), Color(0xFFE8A055))
    val CriticalDark = UrgencyAccent(Color(0xFF3A241C), Color(0xFFE0805F))
}

/** True when the dark theme is active; used to pick dark accent variants. */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

/** Category accent for the current theme (light or dark). */
@Composable
@ReadOnlyComposable
fun categoryAccentFor(key: String?): CategoryAccent =
    if (LocalIsDarkTheme.current) CategoryColors.forKeyDark(key) else CategoryColors.forKey(key)

/** Urgency accent for the current theme (light or dark). */
@Composable
@ReadOnlyComposable
fun urgencyAccentFor(urgency: Urgency): UrgencyAccent = if (LocalIsDarkTheme.current) {
    when (urgency) {
        Urgency.LOW -> UrgencyColors.LowDark
        Urgency.MEDIUM -> UrgencyColors.MediumDark
        Urgency.HIGH -> UrgencyColors.HighDark
        Urgency.CRITICAL -> UrgencyColors.CriticalDark
    }
} else {
    when (urgency) {
        Urgency.LOW -> UrgencyColors.Low
        Urgency.MEDIUM -> UrgencyColors.Medium
        Urgency.HIGH -> UrgencyColors.High
        Urgency.CRITICAL -> UrgencyColors.Critical
    }
}
