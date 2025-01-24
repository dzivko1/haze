package io.github.dzivko1.haze.server.data.item

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.domain.inventory.model.Inventory
import io.github.dzivko1.haze.domain.item.model.ItemClass
import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import io.github.dzivko1.haze.server.data.item.model.*
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import io.github.dzivko1.haze.server.domain.hazeApp.getInitialInventorySize
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.exceptions.ClientException
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.containsId
import io.github.dzivko1.haze.server.util.suspendTransaction
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class DbItemRepository : ItemRepository {

  override suspend fun defineItems(appId: Int, items: List<DefineItemsRequest.Item>): List<Long> {
    return suspendTransaction {
      ItemClassesTable.batchUpsert(
        data = items,
        onUpdateExclude = listOf(ItemClassesTable.id)
      ) { itemClass ->
        if (itemClass.id != null) this[ItemClassesTable.id] = itemClass.id!!
        this[ItemClassesTable.app] = appId
        this[ItemClassesTable.name] = itemClass.name
        this[ItemClassesTable.smallImageUrl] = itemClass.smallImageUrl
        this[ItemClassesTable.largeImageUrl] = itemClass.largeImageUrl
      }.map { it[ItemClassesTable.id].value }
    }
  }

  override suspend fun getItemDefinition(appId: Int): List<ItemClass> {
    return suspendTransaction {
      ItemClassDao.find { ItemClassesTable.app eq appId }.map {
        ItemClass(
          id = it.id.value,
          name = it.name,
          smallImageUrl = it.smallImageUrl,
          largeImageUrl = it.largeImageUrl
        )
      }
    }
  }

  override suspend fun createItems(items: List<CreateItemsRequest.Item>): List<Long> {
    return suspendTransaction {
      // Unify all items to specify their target inventories directly (by inventoryId)
      val processedItems = items.mapNotNull { item ->
        when (item) {
          is CreateItemsRequest.DirectItemDesignation -> item
          is CreateItemsRequest.IndirectItemDesignation -> {
            val inventoryId = getInventoryId(item.userId, item.appId)
              ?: kotlin.runCatching {
                createInventory(item.userId, item.appId)
              }.getOrElse { return@mapNotNull null }
            CreateItemsRequest.DirectItemDesignation(item.itemClassId, inventoryId)
          }
        }
      }

      ItemsTable.batchInsert(processedItems) { item ->
        this[ItemsTable.itemClass] = item.itemClassId
        this[ItemsTable.inventory] = item.inventoryId
        this[ItemsTable.slotIndex] = findFirstAvailableSlotIndex(item.inventoryId)
      }.map { it[ItemsTable.id].value }
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

  override suspend fun getInventory(id: Long): Inventory? {
    return suspendTransaction {
      InventoryDao.findById(id)?.let { inventory ->
        Inventory(
          id = id,
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
    return getInventoryId(userId, appId)
      ?.let { getInventory(it) }
  }

  override suspend fun getInventoryId(userId: Uuid, appId: Int): Long? {
    return suspendTransaction {
      InventoriesTable.select(InventoriesTable.id)
        .where { InventoriesTable.user eq userId.toJavaUuid() }
        .andWhere { InventoriesTable.app eq appId }
        .firstOrNull()?.get(InventoriesTable.id)?.value
    }
  }

  override suspend fun createInventory(userId: Uuid, appId: Int): Long {
    return suspendTransaction {
      if (!UsersTable.containsId(userId)) throw ClientException(ErrorCode.UserNotFound)
      if (!HazeAppsTable.containsId(appId)) throw ClientException(ErrorCode.AppNotFound)

      InventoryDao.new {
        this.user = EntityID(userId.toJavaUuid(), UsersTable)
        this.app = EntityID(appId, HazeAppsTable)
        this.size = getInitialInventorySize(appId)
      }.id.value
    }
  }

  override suspend fun swapItems(inventoryId: Long, indexA: Int, indexB: Int) {
    suspendTransaction {
      if (!InventoriesTable.containsId(inventoryId)) throw ClientException(ErrorCode.InventoryNotFound)

      val itemA = ItemDao.find { ItemsTable.inventory eq inventoryId and (ItemsTable.slotIndex eq indexA) }
        .forUpdate().singleOrNull()
      val itemB = ItemDao.find { ItemsTable.inventory eq inventoryId and (ItemsTable.slotIndex eq indexB) }
        .forUpdate().singleOrNull()

      itemA?.slotIndex = indexB
      itemB?.slotIndex = indexA
    }
  }
}