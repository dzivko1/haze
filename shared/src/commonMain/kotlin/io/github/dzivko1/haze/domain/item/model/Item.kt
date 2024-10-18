package io.github.dzivko1.haze.domain.item.model

import kotlin.uuid.Uuid

data class Item(
  val id: Long,
  val itemClassId: Long,
  val ownerId: Uuid,
  val slotIndex: Int,
)
