package io.github.dzivko1.haze.data.item.model

import io.github.dzivko1.haze.data.UuidSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CreateItemsRequest(
  val items: List<Item>,
) {
  @Serializable
  data class Item(
    val itemClassId: Long,
    @Serializable(with = UuidSerializer::class)
    val ownerId: Uuid,
  )
}

