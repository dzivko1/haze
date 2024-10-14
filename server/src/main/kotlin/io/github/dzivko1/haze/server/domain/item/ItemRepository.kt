package io.github.dzivko1.haze.server.domain.item

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest

interface ItemRepository {

  /**
   * Creates/updates the item class definition.
   * - If the ID is not specified for an item, a new item class is created with an auto-generated ID.
   * - If an existing ID is provided, the corresponding item class is updated.
   * - If a nonexistent ID is provided, a new item class is created with that ID.
   *
   * @return A list of all affected item class IDs.
   */
  suspend fun defineItems(items: List<DefineItemsRequest.Item>): List<Long>

  /**
   * Creates new instances of items belonging to the given users.
   */
  suspend fun createItems(items: List<CreateItemsRequest.Item>): List<Long>
}