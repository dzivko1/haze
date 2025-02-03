package io.github.dzivko1.haze.data.item.model

import io.github.dzivko1.haze.data.UuidSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.uuid.Uuid

@Serializable
data class CreateItemsRequest(
  val items: List<Item>,
) {
  @Serializable(with = ItemSerializer::class)
  sealed interface Item

  @Serializable
  data class DirectItemDesignation(
    val itemClassId: Long,
    val inventoryId: Long,
  ) : Item

  @Serializable
  data class IndirectItemDesignation(
    val itemClassId: Long,
    @Serializable(with = UuidSerializer::class)
    val userId: Uuid,
    val appId: Int,
  ) : Item
}

@Serializable
data class CreateItemsResponse(
  val itemIds: List<Long>
)

private object ItemSerializer : JsonContentPolymorphicSerializer<CreateItemsRequest.Item>(CreateItemsRequest.Item::class) {
  override fun selectDeserializer(element: JsonElement): DeserializationStrategy<CreateItemsRequest.Item> {
    return when {
      "inventoryId" in element.jsonObject -> CreateItemsRequest.DirectItemDesignation.serializer()
      "userId" in element.jsonObject && "appId" in element.jsonObject -> CreateItemsRequest.IndirectItemDesignation.serializer()
      else -> throw SerializationException("Unknown item type")
    }
  }
}
