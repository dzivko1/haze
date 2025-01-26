package io.github.dzivko1.haze.domain.inventory.model

import io.github.dzivko1.haze.data.UuidSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Inventory(
  val id: Long,
  @Serializable(with = UuidSerializer::class)
  val ownerId: Uuid,
  val appId: Int,
  val size: Int,
  val items: List<Item>,
) {
  @Serializable
  data class Item(
    val id: Long,
    val itemClassId: Long,
    val slotIndex: Int?,
  )
}
