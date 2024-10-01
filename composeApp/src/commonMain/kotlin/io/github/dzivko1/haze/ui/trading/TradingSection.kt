package io.github.dzivko1.haze.ui.trading

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object TradingSectionRoute

fun NavGraphBuilder.tradingSection() {
  navigation<TradingSectionRoute>(startDestination = TradingRoute) {
    tradingRoute()
  }
}