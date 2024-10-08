package io.github.dzivko1.haze.client.ui.inventory

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dzivko1.haze.client.ui.inventory.composable.InventoryScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object InventoryRoute

fun NavGraphBuilder.inventoryRoute() {
  composable<InventoryRoute> {
    val viewModel = koinViewModel<InventoryViewModel>()
    val uiState = viewModel.uiState

    InventoryScreen(
      uiState = uiState,
      onSlotClick = {}
    )
  }
}