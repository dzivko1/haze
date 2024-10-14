package io.github.dzivko1.haze.server.data.item

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.server.data.item.model.ItemClassesTable
import io.github.dzivko1.haze.server.data.item.model.ItemsTable
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.util.suspendTransaction
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.batchUpsert
import kotlin.uuid.toJavaUuid

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
        this[ItemsTable.owner] = item.ownerId.toJavaUuid()
      }.map { it[ItemsTable.id].value }
    }
  }
}