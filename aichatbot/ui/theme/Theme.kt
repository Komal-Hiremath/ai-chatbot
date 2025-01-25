package com.example.aichatbot.ui.theme

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
import androidx.compose.material3.Typography

// Define custom colors
val PrimaryDark = Color(0xFF6200EE)
val OnPrimaryDark = Color(0xFFFFFFFF)
val PrimaryLight = Color(0xFF6200EE)
val OnPrimaryLight = Color(0xFFFFFFFF)
val BackgroundDark = Color(0xFF121212)
val OnBackgroundDark = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF000000)

// Dark color scheme
val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark
)

// Light color scheme
val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight
)

// Main Theme function
@Composable
fun AIChatbotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Use dynamic colors on Android 12+
    content: @Composable () -> Unit
) {
    // Determine the color scheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Apply theme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Material typography
        content = content
    )
}
