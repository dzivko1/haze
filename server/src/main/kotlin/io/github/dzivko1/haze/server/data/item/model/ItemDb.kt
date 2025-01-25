package io.github.dzivko1.haze.server.data.item.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ItemsTable : LongIdTable("items") {
  val originalId = long("original_id").default(-1)
  val nextId = reference("next_id", ItemsTable).nullable()
  val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
  val itemClass = reference("item_class", ItemClassesTable)
  val inventory = reference("inventory", InventoriesTable)
  val slotIndex = integer("slot_index")
}

class ItemDao(id: EntityID<Long>) : LongEntity(id) {
  var originalId by ItemsTable.originalId
  var nextId by ItemsTable.nextId
  var createdAt by ItemsTable.createdAt
  var itemClass by ItemClassDao referencedOn ItemsTable.itemClass
  var inventory by InventoryDao referencedOn ItemsTable.inventory
  var slotIndex by ItemsTable.slotIndex

  companion object : LongEntityClass<ItemDao>(ItemsTable)
}