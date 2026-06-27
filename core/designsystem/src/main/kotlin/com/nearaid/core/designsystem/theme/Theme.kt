package com.nearaid.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val NearAidColorScheme = lightColorScheme(
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

val NearAidShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

@Composable
fun NearAidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NearAidColorScheme,
        typography = NearAidTypography,
        shapes = NearAidShapes,
        content = content,
    )
}
