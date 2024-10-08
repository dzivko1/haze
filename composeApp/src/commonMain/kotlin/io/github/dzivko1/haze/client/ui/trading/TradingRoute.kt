package io.github.dzivko1.haze.client.ui.trading

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object TradingRoute

fun NavGraphBuilder.tradingRoute() {
  composable<TradingRoute> {

  }
}