package com.pratikbhosale.daybook.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Ink, // Simple inversion placeholder (Dark paper)
    surface = Ink,
    onBackground = Paper,
    onSurface = Paper,
    primary = TodayFill,
    onPrimary = TodayInk
)

private val LightColorScheme = lightColorScheme(
    background = Paper,
    surface = Paper,
    onBackground = Ink,
    onSurface = Ink,
    primary = TodayFill,
    onPrimary = TodayInk
)

@Composable
fun DaybookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // We want strict brand colors by default per DESIGN.md
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

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
