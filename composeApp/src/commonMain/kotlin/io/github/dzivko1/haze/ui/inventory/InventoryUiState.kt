package io.github.dzivko1.haze.ui.inventory

import androidx.compose.ui.graphics.ImageBitmap

data class InventoryUiState(
  val itemSlots: List<ItemSlotUi> = emptyList(),
)

data class ItemSlotUi(
  val item: ItemUi? = null,
)

data class ItemUi(
  val id: String,
  val image: ImageBitmap,
  val name: String,
)
