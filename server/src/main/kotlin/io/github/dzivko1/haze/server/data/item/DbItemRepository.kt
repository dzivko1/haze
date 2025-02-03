package io.github.dzivko1.haze.server.data.item

import io.github.aakira.napier.Napier
import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.domain.inventory.model.Inventory
import io.github.dzivko1.haze.domain.item.model.ItemClass
import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import io.github.dzivko1.haze.server.data.item.model.InventoriesTable
import io.github.dzivko1.haze.server.data.item.model.InventoryDao
import io.github.dzivko1.haze.server.data.item.model.ItemClassDao
import io.github.dzivko1.haze.server.data.item.model.ItemClassesTable
import io.github.dzivko1.haze.server.data.item.model.ItemDao
import io.github.dzivko1.haze.server.data.item.model.ItemsTable
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import io.github.dzivko1.haze.server.domain.hazeApp.getInitialInventorySize
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.exceptions.ClientException
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.containsId
import io.github.dzivko1.haze.server.util.suspendTransaction
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.statements.BatchUpdateStatement
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class DbItemRepository : ItemRepository {

  override suspend fun defineItems(appId: Int, items: List<DefineItemsRequest.Item>): List<Long> {
    // TODO overwrite the definition instead of upserting
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
      val processedItems = unifyAndFilterItemDesignations(items)

      ItemsTable.batchInsert(processedItems) { item ->
        this[ItemsTable.itemClass] = item.itemClassId
        this[ItemsTable.inventory] = item.inventoryId
      }.map { it[ItemsTable.id].value }
        // Manually set originalId to match the auto-generated ID since postgres doesn't support this
        .also { ids ->
          if (ids.isNotEmpty()) {
            BatchUpdateStatement(ItemsTable).apply {
              ids.forEach { id ->
                addBatch(EntityID(id, ItemsTable))
                this[ItemsTable.originalId] = id
              }
              execute(this@suspendTransaction)
            }
          }
        }
    }
  }

  /**
   * Unifies all items so that they specify their target inventories directly (by inventoryId).
   * Also filters out invalid item/app combinations.
   */
  private suspend fun unifyAndFilterItemDesignations(items: List<CreateItemsRequest.Item>): List<CreateItemsRequest.DirectItemDesignation> {
    return items.mapNotNull { designation ->
      val appOfItem = ItemClassesTable.select(ItemClassesTable.app)
        .where { ItemClassesTable.id eq designation.itemClassId }
        .singleOrNull()?.get(ItemClassesTable.app)?.value

      when (designation) {
        is CreateItemsRequest.DirectItemDesignation -> {
          val targetApp = getInventory(designation.inventoryId)?.appId
          if (appOfItem == targetApp) designation else null
        }

        is CreateItemsRequest.IndirectItemDesignation -> {
          val inventoryId = getInventoryId(designation.userId, designation.appId)
            ?: runCatching {
              createInventory(designation.userId, designation.appId)
            }.getOrElse {
              Napier.e("Could not create missing inventory", it)
              return@mapNotNull null
            }

          if (appOfItem == designation.appId) {
            CreateItemsRequest.DirectItemDesignation(designation.itemClassId, inventoryId)
          } else null
        }
      }
    }
  }

  override suspend fun acceptNewItems(inventoryId: Long) {
    suspendTransaction {
      val inventorySize = InventoryDao.findById(inventoryId)?.size
        ?: throw ClientException(ErrorCode.InventoryNotFound)

      val newItemIds = ItemsTable.select(ItemsTable.id)
        .where { ItemsTable.inventory eq inventoryId }
        .andWhere { ItemsTable.slotIndex eq null }
        .map { it[ItemsTable.id] }

      val filledSlotIndices = ItemsTable.select(ItemsTable.slotIndex)
        .where { ItemsTable.inventory eq inventoryId }
        .mapNotNull { it[ItemsTable.slotIndex] }.toSet()

      val freeSlotCount = inventorySize - filledSlotIndices.size

      BatchUpdateStatement(ItemsTable).let { batch ->
        newItemIds.take(freeSlotCount).forEachIndexed { index, id ->
          batch.addBatch(id)
          batch[ItemsTable.slotIndex] = findFirstAvailableSlotIndex(inventorySize, filledSlotIndices, index)
        }
        if (batch.data.size > 0) batch.execute(this@suspendTransaction)
      }
    }
  }

  private fun findFirstAvailableSlotIndex(inventorySize: Int, filledSlots: Set<Int>, skipCount: Int): Int? {
    val allSlots = 0 until inventorySize
    val freeSlots = allSlots - filledSlots
    return freeSlots.getOrNull(skipCount)
  }

  override suspend fun getInventory(id: Long): Inventory? {
    return suspendTransaction {
      InventoryDao.findById(id)?.let { inventory ->
        Inventory(
          id = id,
          ownerId = inventory.user.id.value.toKotlinUuid(),
          appId = inventory.app.id.value,
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

      InventoriesTable.insertAndGetId {
        it[InventoriesTable.user] = userId.toJavaUuid()
        it[InventoriesTable.app] = appId
        it[InventoriesTable.size] = getInitialInventorySize(appId)
      }.value
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