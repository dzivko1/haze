package io.github.dzivko1.haze.server.data.item.model

import io.github.dzivko1.haze.server.data.user.model.UserDao
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ItemsTable : LongIdTable("items") {
  val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
  val itemClass = reference("item_class", ItemClassesTable)
  val owner = reference("owner", UsersTable)
}

class ItemDao(id: EntityID<Long>) : LongEntity(id) {
  var createdAt by ItemsTable.createdAt
  var itemClass by ItemClassDao referencedOn ItemsTable.itemClass
  var owner by UserDao referencedOn ItemsTable.owner

  companion object : LongEntityClass<ItemDao>(ItemsTable)
}