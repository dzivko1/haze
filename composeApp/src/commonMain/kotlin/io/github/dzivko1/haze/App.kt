package io.github.dzivko1.haze

import androidx.compose.runtime.Composable
import io.github.dzivko1.haze.di.UiModule
import io.github.dzivko1.haze.ui.AppUi
import io.github.dzivko1.haze.ui.theme.AppTheme
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