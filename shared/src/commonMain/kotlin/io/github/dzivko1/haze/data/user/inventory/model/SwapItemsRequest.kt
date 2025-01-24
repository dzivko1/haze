package io.github.dzivko1.haze.data.user.inventory.model

import kotlinx.serialization.Serializable

@Serializable
data class SwapItemsRequest(
  val indexA: Int,
  val indexB: Int
)
