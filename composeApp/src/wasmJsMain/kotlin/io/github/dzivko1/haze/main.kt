package io.github.dzivko1.haze

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  initApp()

  ComposeViewport(document.body!!) {
    App()
  }
}