package io.github.dzivko1.haze.client

import androidx.compose.runtime.Composable
import io.github.dzivko1.haze.client.di.UiModule
import io.github.dzivko1.haze.client.ui.AppUi
import io.github.dzivko1.haze.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initApp(koinInitializer: KoinAppDeclaration = {}) {
  startKoin {
    koinInitializer()
    modules(UiModule)
  }
}

@Composable
@Preview
fun App() {
  KoinContext {
    AppTheme {
      AppUi()
    }
  }
}