package com.nearaid.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ---- Brand palette (mirrors the :root design tokens in the UI spec) ----
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

// ---- Category accents (.c-* in the spec) ----
data class CategoryAccent(val container: Color, val content: Color)

object CategoryColors {
    val Food = CategoryAccent(MarigoldTint, MarigoldDeep)
    val Clothes = CategoryAccent(BlueTint, BlueAccent)
    val Medicine = CategoryAccent(RustTint, Rust)
    val Goods = CategoryAccent(TealTint, Teal)
    val Shelter = CategoryAccent(Color(0xFFEAE4F3), Color(0xFF6A4DA3))
    val Other = CategoryAccent(Color(0xFFEEEEEE), Color(0xFF777777))

    fun forKey(key: String?): CategoryAccent = when (key) {
        "food" -> Food
        "clothes" -> Clothes
        "medicine" -> Medicine
        "goods" -> Goods
        "shelter" -> Shelter
        else -> Other
    }
}

// ---- Urgency tags (.t-* in the spec) ----
object UrgencyColors {
    val LowContainer = Color(0xFFEAEFEA)
    val LowContent = Color(0xFF5C7A5C)
    val MediumContainer = MarigoldTint
    val MediumContent = MarigoldDeep
    val HighContainer = Color(0xFFFBE4D2)
    val HighContent = Color(0xFFB8651F)
    val CriticalContainer = RustTint
    val CriticalContent = Rust
}
