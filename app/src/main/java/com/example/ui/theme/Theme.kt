package com.example.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalHaqooqDarkMode = staticCompositionLocalOf { true }

// Material 3 Expressive Spring Motion Specs (Spatial & Effects)
object HaqooqMotion {
    fun <T> spatialSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    fun <T> fastEffectsSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

private val HaqooqDarkColorScheme = darkColorScheme(
    primary = DarkEmeraldPrimary,
    onPrimary = DarkEmeraldOnPrimary,
    primaryContainer = DarkEmeraldContainer,
    onPrimaryContainer = DarkEmeraldOnContainer,
    secondary = DarkGoldSecondary,
    onSecondary = DarkGoldOnSecondary,
    secondaryContainer = DarkGoldContainer,
    onSecondaryContainer = DarkGoldOnContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkMutedInk,
    outline = DarkOutline,
    outlineVariant = DarkOutline.copy(alpha = 0.6f),
    error = EmergencyRedDark,
    onError = DarkBackground,
    errorContainer = EmergencyRedDarkContainer,
    onErrorContainer = EmergencyRedContainer
)

private val HaqooqLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldOnContainer,
    secondary = GoldSecondary,
    onSecondary = GoldOnSecondary,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldOnContainer,
    tertiary = SlateTertiary,
    onTertiary = SlateOnTertiary,
    tertiaryContainer = SlateContainer,
    onTertiaryContainer = SlateOnContainer,
    background = AlabasterBackground,
    onBackground = CharcoalOnBackground,
    surface = WarmSurface,
    onSurface = CharcoalOnBackground,
    surfaceContainerLowest = WarmSurface,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    surfaceVariant = SandstoneSurfaceVariant,
    onSurfaceVariant = MutedInk,
    outline = WarmOutline,
    outlineVariant = WarmOutline.copy(alpha = 0.6f),
    error = EmergencyRed,
    onError = EmeraldOnPrimary,
    errorContainer = EmergencyRedContainer,
    onErrorContainer = EmergencyOnRedContainer
)

// Material 3 Expressive Shapes — bold tactile curves
val HaqooqShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(34.dp)
)

@Composable
fun HaqooqTheme(
    darkTheme: Boolean = true,
    isUrdu: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HaqooqDarkColorScheme else HaqooqLightColorScheme
    val typography = haqooqTypography(isUrdu = isUrdu)

    CompositionLocalProvider(LocalHaqooqDarkMode provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = HaqooqShapes,
            content = content
        )
    }
}
