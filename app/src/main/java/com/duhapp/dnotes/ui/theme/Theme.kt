package com.duhapp.dnotes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = White,
    primaryContainer = PrimaryDarkColor,
    onPrimaryContainer = LightPrimary,

    secondary = SecondaryColor,
    onSecondary = White,
    secondaryContainer = SecondaryDarkColor,
    onSecondaryContainer = LightSecondary,

    tertiary = BasePrimary,
    onTertiary = White,
    tertiaryContainer = DarkPrimary,
    onTertiaryContainer = LightPrimary,

    error = BaseError,
    onError = White,
    errorContainer = DarkError,
    onErrorContainer = LightError,

    background = Black,
    onBackground = White,

    surface = Black,
    onSurface = White,
    surfaceVariant = DarkGrey,
    onSurfaceVariant = LightGrey,

    outline = BaseGrey,
    outlineVariant = DarkGrey
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = White,
    primaryContainer = PrimaryLightColor,
    onPrimaryContainer = DarkPrimary,

    secondary = SecondaryColor,
    onSecondary = White,
    secondaryContainer = SecondaryLightColor,
    onSecondaryContainer = DarkSecondary,

    tertiary = BasePrimary,
    onTertiary = White,
    tertiaryContainer = LightPrimary,
    onTertiaryContainer = DarkPrimary,

    error = BaseError,
    onError = White,
    errorContainer = LightError,
    onErrorContainer = DarkError,

    background = BackgroundColor,
    onBackground = Black,

    surface = White,
    onSurface = Black,
    surfaceVariant = LightGrey,
    onSurfaceVariant = BaseGrey,

    outline = BaseGrey,
    outlineVariant = LightGrey
)

@Composable
fun DNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
