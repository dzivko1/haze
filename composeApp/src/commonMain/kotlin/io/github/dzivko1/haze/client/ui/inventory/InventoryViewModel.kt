package io.github.dzivko1.haze.client.ui.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class InventoryViewModel : ViewModel() {
  val uiState by mutableStateOf(InventoryUiState(itemSlots = List(200) { ItemSlotUi() }))
}