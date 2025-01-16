package io.github.dzivko1.haze.domain.item.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemClass(
  val id: Long,
  val name: String,
  val smallImageUrl: String,
  val largeImageUrl: String,
)
