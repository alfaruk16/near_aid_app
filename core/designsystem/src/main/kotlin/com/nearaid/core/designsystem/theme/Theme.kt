package com.nearaid.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp

private val NearAidLightColorScheme = lightColorScheme(
    primary = Marigold,
    onPrimary = OnMarigold,
    primaryContainer = MarigoldSoft,
    onPrimaryContainer = MarigoldDeep,
    secondary = Teal,
    onSecondary = Surface,
    secondaryContainer = TealSoft,
    onSecondaryContainer = Teal,
    tertiary = BlueAccent,
    error = Rust,
    onError = Surface,
    errorContainer = RustTint,
    onErrorContainer = Rust,
    background = Paper,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = Line2,
    onSurfaceVariant = Ink2,
    outline = Line,
    outlineVariant = Line2,
)

private val NearAidDarkColorScheme = darkColorScheme(
    primary = MarigoldDark,
    onPrimary = OnMarigold,
    primaryContainer = MarigoldTintDark,
    onPrimaryContainer = MarigoldDeepDark,
    secondary = TealDark,
    onSecondary = PaperDark,
    secondaryContainer = TealTintDark,
    onSecondaryContainer = TealDark,
    tertiary = BlueAccentDark,
    error = RustDark,
    onError = PaperDark,
    errorContainer = RustTintDark,
    onErrorContainer = RustDark,
    background = PaperDark,
    onBackground = InkDark,
    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = Line2Dark,
    onSurfaceVariant = Ink2Dark,
    outline = LineDark,
    outlineVariant = Line2Dark,
)

val NearAidShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

/**
 * Accessor for NearAid's semantic colors, e.g. `NearAidTheme.colors.ink`.
 * Coexists with the [NearAidTheme] composable (function and object share the name,
 * mirroring Material3's `MaterialTheme`).
 */
object NearAidTheme {
    val colors: NearAidColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNearAidColors.current
}

@Composable
fun NearAidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val nearAidColors = if (darkTheme) DarkNearAidColors else LightNearAidColors
    val colorScheme = if (darkTheme) NearAidDarkColorScheme else NearAidLightColorScheme

    CompositionLocalProvider(LocalNearAidColors provides nearAidColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NearAidTypography,
            shapes = NearAidShapes,
            content = content,
        )
    }
}
