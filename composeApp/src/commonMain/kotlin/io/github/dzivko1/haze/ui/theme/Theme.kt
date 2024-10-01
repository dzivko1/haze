package io.github.dzivko1.haze.ui.theme

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
      tertiary = Color(0xFF154A5D)
    ),
    content = content
  )
}