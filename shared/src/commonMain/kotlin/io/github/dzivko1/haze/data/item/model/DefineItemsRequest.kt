package io.github.dzivko1.haze.data.item.model

import kotlinx.serialization.Serializable

@Serializable
data class DefineItemsRequest(
  val items: List<Item>,
) {
  @Serializable
  data class Item(
    val id: Long? = null,
    val appId: Int,
    val name: String,
    val iconUrl: String,
  )
}
