package io.github.dzivko1.haze.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.dzivko1.haze.ui.home.homeSection
import io.github.dzivko1.haze.ui.inventory.InventorySectionRoute
import io.github.dzivko1.haze.ui.inventory.inventorySection
import io.github.dzivko1.haze.ui.profile.profileSection
import io.github.dzivko1.haze.ui.store.storeSection
import io.github.dzivko1.haze.ui.trading.tradingSection

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