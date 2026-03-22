package com.duhapp.dnotes.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = White,
    primaryContainer = LightPrimary,
    onPrimaryContainer = DarkPrimary,
    secondary = SecondaryColor,
    onSecondary = White,
    secondaryContainer = LightSecondary,
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
    outline = DarkGrey
)

@Composable
fun DNotesTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
