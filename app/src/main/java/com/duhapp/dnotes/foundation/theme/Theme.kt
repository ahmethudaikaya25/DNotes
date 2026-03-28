package com.duhapp.dnotes.foundation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Light color scheme ─────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = DNotesIndigo40,
    onPrimary          = Color.White,
    primaryContainer   = DNotesIndigo90,
    onPrimaryContainer = DNotesIndigo10,

    secondary            = DNotesViolet40,
    onSecondary          = Color.White,
    secondaryContainer   = DNotesViolet90,
    onSecondaryContainer = DNotesViolet10,

    tertiary            = DNotesTeal40,
    onTertiary          = Color.White,
    tertiaryContainer   = DNotesTeal90,
    onTertiaryContainer = Color(0xFF00201C),

    error            = DNotesError40,
    onError          = Color.White,
    errorContainer   = DNotesError90,
    onErrorContainer = Color(0xFF410002),

    background    = DNotesNeutral99,
    onBackground  = DNotesNeutral10,
    surface       = DNotesNeutral99,
    onSurface     = DNotesNeutral10,
    surfaceVariant   = DNotesNeutralVariant90,
    onSurfaceVariant = DNotesNeutralVariant30,
    outline          = DNotesNeutralVariant50,
)

// ── Dark color scheme ──────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = DNotesIndigo80,
    onPrimary          = DNotesIndigo20,
    primaryContainer   = DNotesIndigo30,
    onPrimaryContainer = DNotesIndigo90,

    secondary            = DNotesViolet80,
    onSecondary          = DNotesViolet20,
    secondaryContainer   = DNotesViolet30,
    onSecondaryContainer = DNotesViolet90,

    tertiary            = DNotesTeal80,
    onTertiary          = Color(0xFF003731),
    tertiaryContainer   = Color(0xFF005048),
    onTertiaryContainer = DNotesTeal90,

    error            = DNotesError80,
    onError          = Color(0xFF690005),
    errorContainer   = Color(0xFF93000A),
    onErrorContainer = DNotesError90,

    background    = DNotesNeutral10,
    onBackground  = DNotesNeutral90,
    surface       = DNotesNeutral10,
    onSurface     = DNotesNeutral90,
    surfaceVariant   = DNotesNeutralVariant30,
    onSurfaceVariant = DNotesNeutralVariant80,
    outline          = DNotesNeutralVariant80,
)

/**
 * Main theme composable for DNotes.
 *
 * - Uses **Dynamic Color** on Android 12+ (personalised to the user's wallpaper)
 * - Falls back to the hand-crafted indigo/violet palette on older devices
 * - Applies status bar colour via SystemUiController
 *
 * Usage:
 * ```
 * DNotesTheme {
 *     AppNavGraph()
 * }
 * ```
 */
@Composable
fun DNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = DNotesTypography,
        content     = content,
    )
}
