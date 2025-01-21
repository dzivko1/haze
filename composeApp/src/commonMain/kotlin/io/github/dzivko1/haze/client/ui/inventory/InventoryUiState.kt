package io.github.dzivko1.haze.client.ui.inventory

data class InventoryUiState(
  val isLoading: Boolean = true,
  val itemSlots: List<ItemSlotUi> = emptyList(),
)

data class ItemSlotUi(
  val item: ItemUi? = null,
)

data class ItemUi(
  val id: Long,
  val name: String?,
  val imageUrl: String?,
)
