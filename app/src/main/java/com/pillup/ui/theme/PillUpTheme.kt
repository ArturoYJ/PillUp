package com.pillup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// 🎨 Paleta de colores base de PiilUp (puedes ajustarla después)
private val LightColors = lightColorScheme(
    primary = AzulFuerte,
    secondary = AzulMedio,
    tertiary = AzulClaro,

)

private val DarkColors = darkColorScheme(
    primary = AzulFuerte,
    secondary = AzulMedio,
    tertiary = AzulClaro,

)

// 🎨 Theme global de PiilUp
@Composable
fun PillUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors =
        if (darkTheme) DarkColors
        else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
