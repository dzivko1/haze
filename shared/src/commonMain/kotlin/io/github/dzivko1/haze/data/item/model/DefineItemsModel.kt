package io.github.dzivko1.haze.data.item.model

import kotlinx.serialization.Serializable

@Serializable
data class DefineItemsRequest(
  val appId: Int,
  val items: List<Item>,
) {
  @Serializable
  data class Item(
    val id: Long? = null,
    val name: String,
    val smallImageUrl: String,
    val largeImageUrl: String,
  )
}

@Serializable
data class DefineItemsResponse(
  val itemClassIds: List<Long>
)
