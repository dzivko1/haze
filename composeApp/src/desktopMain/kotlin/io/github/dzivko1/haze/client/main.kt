package io.github.dzivko1.haze.client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import haze.composeapp.generated.resources.Res
import haze.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() {
  initApp()

  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = stringResource(Res.string.app_name),
    ) {
      App()
    }
  }
}