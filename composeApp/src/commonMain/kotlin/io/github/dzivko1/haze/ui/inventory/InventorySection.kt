package io.github.dzivko1.haze.ui.inventory

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object InventorySectionRoute

fun NavGraphBuilder.inventorySection() {
  navigation<InventorySectionRoute>(startDestination = InventoryRoute) {
    inventoryRoute()
  }
}