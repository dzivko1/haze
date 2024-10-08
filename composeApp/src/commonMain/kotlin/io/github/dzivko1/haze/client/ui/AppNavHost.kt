package io.github.dzivko1.haze.client.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.dzivko1.haze.client.ui.home.homeSection
import io.github.dzivko1.haze.client.ui.inventory.InventorySectionRoute
import io.github.dzivko1.haze.client.ui.inventory.inventorySection
import io.github.dzivko1.haze.client.ui.profile.profileSection
import io.github.dzivko1.haze.client.ui.store.storeSection
import io.github.dzivko1.haze.client.ui.trading.tradingSection

@Composable
fun AppNavHost(
  navController: NavHostController,
) {
  NavHost(
    navController = navController,
    startDestination = InventorySectionRoute
  ) {
    homeSection()
    storeSection()
    inventorySection()
    tradingSection()
    profileSection()
  }
}