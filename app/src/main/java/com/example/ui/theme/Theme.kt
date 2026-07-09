package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FrostedGlassColorScheme = darkColorScheme(
    primary = SuccessGreen,
    secondary = EmeraldGreen,
    tertiary = SixPurple,
    background = NearBlack,
    surface = CardBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = DismissalRed
)

@Composable
fun GullyCrixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FrostedGlassColorScheme,
        typography = Typography,
        content = content
    )
}

