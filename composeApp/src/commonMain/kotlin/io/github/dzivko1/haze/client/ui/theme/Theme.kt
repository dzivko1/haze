package io.github.dzivko1.haze.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = darkColorScheme(
      primary = Color(0xFF000000),
      secondary = Color(0xFF088C4D),
      tertiary = Color(0xFF154A5D),
      background = Color(0xFF1D1C1B),
      surface = Color(0xFF161514),
      surfaceBright = Color(0xFF50463d),
      surfaceDim = Color(0xFF181512),
      surfaceContainerLowest = Color(0xFF181512),
      surfaceContainerLow = Color(0xFF2b2621),
      surfaceContainer = Color(0xFF322c26),
      surfaceContainerHigh = Color(0xFF3e372f),
      surfaceContainerHighest = Color(0xFF50463d),
      outline = Color(0xFF21201e),
      outlineVariant = Color(0xFF262523),
    ),
    content = content
  )
}