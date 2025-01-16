package io.github.dzivko1.haze.server.domain.item

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.domain.inventory.model.Inventory
import io.github.dzivko1.haze.domain.item.model.ItemClass
import kotlin.uuid.Uuid

interface ItemRepository {

  /**
   * Creates/updates the item class definition for the specified app.
   * - If the ID is not specified for an item, a new item class is created with an auto-generated ID.
   * - If an existing ID is provided, the corresponding item class is updated.
   * - If a nonexistent ID is provided, a new item class is created with that ID.
   *
   * @return A list of all affected item class IDs.
   */
  suspend fun defineItems(appId: Int, items: List<DefineItemsRequest.Item>): List<Long>

  /**
   * Gets the item class definition for the specified app.
   */
  suspend fun getItemDefinition(appId: Int): List<ItemClass>

  /**
   * Creates new instances of items belonging to the given users.
   *
   * @return the IDs of all created items.
   */
  suspend fun createItems(items: List<CreateItemsRequest.Item>): List<Long>

  /**
   * Gets items that belong in the given inventory.
   */
  suspend fun getInventory(id: Long): Inventory?

  /**
   * Gets items that belong to the given user's inventory.
   */
  suspend fun getInventory(userId: Uuid, appId: Int): Inventory?

  /**
   * Creates a given user's inventory for the given app.
   *
   * @return the ID of the created inventory.
   */
  suspend fun createInventory(userId: Uuid, appId: Int): Long
}