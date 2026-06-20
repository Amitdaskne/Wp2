package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
  primary = CyberPrimary,
  secondary = CyberSecondary,
  tertiary = CyberAccent,
  background = CyberBg,
  surface = CyberGray,
  onPrimary = CyberBg,
  onSecondary = CyberText,
  onTertiary = CyberText,
  onBackground = CyberText,
  onSurface = CyberText,
  primaryContainer = CyberGray,
  secondaryContainer = CyberBg
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  // Always use Cyberpunk theme
  MaterialTheme(
    colorScheme = CyberColorScheme,
    typography = Typography,
    content = content
  )
}

