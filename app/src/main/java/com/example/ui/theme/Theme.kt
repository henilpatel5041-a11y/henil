package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SoftAquamarine,
    onPrimary = DarkBackgroundZen,
    primaryContainer = SageGreenDark,
    onPrimaryContainer = PaleMintSurface,
    secondary = CalmSkyBlue,
    onSecondary = DarkBackgroundZen,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = PaleMintSurface,
    tertiary = WarmSandLight,
    onTertiary = DarkBackgroundZen,
    background = DarkBackgroundZen,
    onBackground = Color(0xFFE2EBE8),
    surface = DarkSurfaceZen,
    onSurface = Color(0xFFE2EBE8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBACCC6)
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = PaleMintSurface,
    onPrimaryContainer = SageGreenDark,
    secondary = CalmTeal,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = SageGreenDark,
    tertiary = WarmSand,
    onTertiary = Color.White,
    background = LightBackgroundZen,
    onBackground = Color(0xFF142421),
    surface = LightSurfaceZen,
    onSurface = Color(0xFF142421),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF435954)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our custom handcrafted serene colors by default for consistent calming aesthetic
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
        typography = Typography,
        content = content
    )
}
