package io.github.dzivko1.haze.data.item.model

import io.github.dzivko1.haze.domain.item.model.ItemClass
import kotlinx.serialization.Serializable

@Serializable
data class GetItemDefinitionResponse(
  val items: List<ItemClass>
)