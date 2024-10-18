package io.github.dzivko1.haze.data.item.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateItemsRequest(
  val items: List<Item>,
) {
  @Serializable
  data class Item(
    val itemClassId: Long,
    val inventoryId: Long,
  )
}

