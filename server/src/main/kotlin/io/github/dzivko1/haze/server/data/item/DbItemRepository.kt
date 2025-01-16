package io.github.dzivko1.haze.server.data.item

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.domain.inventory.model.Inventory
import io.github.dzivko1.haze.domain.item.model.ItemClass
import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppDao
import io.github.dzivko1.haze.server.data.item.model.*
import io.github.dzivko1.haze.server.data.user.model.UserDao
import io.github.dzivko1.haze.server.domain.hazeApp.getInitialInventorySize
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.exceptions.ClientException
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.suspendTransaction
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.batchUpsert
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

  private suspend fun getInventoryId(userId: Uuid, appId: Int): Long? {
    return suspendTransaction {
      InventoryDao.find { InventoriesTable.user eq userId.toJavaUuid() and (InventoriesTable.app eq appId) }
        .firstOrNull()?.id?.value
    }
  }

  override suspend fun createInventory(userId: Uuid, appId: Int): Long {
    val user = UserDao.findById(userId.toJavaUuid()) ?: throw ClientException(ErrorCode.UserNotFound)
    val app = HazeAppDao.findById(appId) ?: throw ClientException(ErrorCode.AppNotFound)
    return InventoryDao.new {
      this.user = user.id
      this.app = app.id
      this.size = getInitialInventorySize(appId)
    }.id.value
  }
}