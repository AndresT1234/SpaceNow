package com.app.spacenow.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Brown,
    secondary = BeigeDark,
    tertiary = BeigeLight,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = LightGray,
    onSecondary = LightGray,
    onTertiary = Brown,
    onBackground = LightGray,
    onSurface = LightGray,
    secondaryContainer = BeigeDark,
    onSecondaryContainer = LightGray,
    error = Brown
)

private val LightColorScheme = lightColorScheme(
    primary = Brown,
    secondary = BeigeDark,
    tertiary = BeigeLight,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightGray,
    onSecondary = Brown,
    onTertiary = Brown,
    onBackground = Brown,
    onSurface = Brown,
    secondaryContainer = BeigeLight,
    onSecondaryContainer = Brown,
    error = Brown
)

@Composable
fun SpaceNowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}