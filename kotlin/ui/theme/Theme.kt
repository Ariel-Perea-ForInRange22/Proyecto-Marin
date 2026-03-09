package com.devcore.uat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = UATColors.AzulPastel,
    onPrimary = UATColors.Blanco,
    secondary = UATColors.NaranjaPastel,
    onSecondary = UATColors.Blanco,
    tertiary = UATColors.VerdePastel,
    background = UATColors.GrisClaro,
    surface = UATColors.Blanco,
    error = UATColors.RojoPastel,
    onBackground = UATColors.Negro,
    onSurface = UATColors.Negro,
)

@Composable
fun DevCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
