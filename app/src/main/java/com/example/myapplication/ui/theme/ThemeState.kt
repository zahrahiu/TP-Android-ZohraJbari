package com.example.myapplication.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf

enum class Mode { CLAIR, SOMBRE, PASTEL }

class ThemeState {
    var mode by mutableStateOf(Mode.CLAIR)
}

val LocalThemeState = staticCompositionLocalOf { ThemeState() }

val LightScheme = lightColorScheme(
    primary = Color(0xFF989D71),
    background = Color(0xFFFFFBF7),
)

val DarkScheme = darkColorScheme(
    primary = Color(0xFFEF9A9A),
    background = Color(0xFF121212),
)

val PastelScheme = lightColorScheme(
    primary = Color(0xFFB5838D),
    background = Color(0xFFFFF1F3),
)

@Composable
fun AppTheme(themeState: ThemeState, content: @Composable () -> Unit) {
    val colors = when (themeState.mode) {
        Mode.CLAIR -> LightScheme
        Mode.SOMBRE -> DarkScheme
        Mode.PASTEL -> PastelScheme
    }
    CompositionLocalProvider(LocalThemeState provides themeState) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}
