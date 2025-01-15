package io.github.dzivko1.haze.server.data.item

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.domain.inventory.model.Inventory
import io.github.dzivko1.haze.server.data.item.model.*
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.util.suspendTransaction
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.batchUpsert
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class DbItemRepository : ItemRepository {

  override suspend fun defineItems(items: List<DefineItemsRequest.Item>): List<Long> {
    return suspendTransaction {
      ItemClassesTable.batchUpsert(
        data = items,
        onUpdateExclude = listOf(ItemClassesTable.id)
      ) { itemClass ->
        if (itemClass.id != null) this[ItemClassesTable.id] = itemClass.id!!
        this[ItemClassesTable.app] = itemClass.appId
        this[ItemClassesTable.name] = itemClass.name
        this[ItemClassesTable.iconUrl] = itemClass.iconUrl
      }.map { it[ItemClassesTable.id].value }
    }
  }

  override suspend fun createItems(items: List<CreateItemsRequest.Item>): List<Long> {
    return suspendTransaction {
      ItemsTable.batchInsert(items) { item ->
        this[ItemsTable.itemClass] = item.itemClassId
        this[ItemsTable.inventory] = item.inventoryId
        this[ItemsTable.slotIndex] = findFirstAvailableSlotIndex(item.inventoryId)
      }.map { it[ItemsTable.id].value }
    }
  }

  override suspend fun getInventory(id: Long): Inventory? {
    return suspendTransaction {
      InventoryDao.findById(id)?.let { inventory ->
        Inventory(
          ownerId = inventory.user.value.toKotlinUuid(),
          appId = inventory.app.value,
          size = inventory.size,
          items = ItemDao.find { ItemsTable.inventory eq id }
            .orderBy(ItemsTable.slotIndex to SortOrder.ASC)
            .map {
              Inventory.Item(
                id = it.id.value,
                itemClassId = it.itemClass.id.value,
                slotIndex = it.slotIndex
              )
            }
        )
      }
    }
    // TODO convert to DSL?
  }

  override suspend fun getInventory(userId: Uuid, appId: Int): Inventory? {
    return suspendTransaction {
      InventoryDao.find { InventoriesTable.user eq userId.toJavaUuid() and (InventoriesTable.app eq appId) }
        .firstOrNull()
        ?.let { inventory ->
          Inventory(
            ownerId = userId,
            appId = appId,
            size = inventory.size,
            items = ItemDao.find { ItemsTable.inventory eq inventory.id }
              .orderBy(ItemsTable.slotIndex to SortOrder.ASC)
              .map {
                Inventory.Item(
                  id = it.id.value,
                  itemClassId = it.itemClass.id.value,
                  slotIndex = it.slotIndex
                )
              }
          )
        }
    }
  }

  private fun findFirstAvailableSlotIndex(inventoryId: Long): Int {
    return ItemsTable.select(ItemsTable.slotIndex)
      .where { ItemsTable.inventory eq inventoryId }
      .orderBy(ItemsTable.slotIndex, SortOrder.ASC)
      .forUpdate()
      .fold(0) { expectedSlot, row ->
        val slot = row[ItemsTable.slotIndex]
        if (slot == expectedSlot) expectedSlot + 1 else expectedSlot
      }
  }
}