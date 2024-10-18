package io.github.dzivko1.haze.server.data.item.model

import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object InventoriesTable : LongIdTable("inventories") {
  val user = reference("user", UsersTable)
  val app = reference("app", HazeAppsTable)
  val size = integer("size").check { it greater 0 }
}

class InventoryDao(id: EntityID<Long>) : LongEntity(id) {
  var user by InventoriesTable.user
  var app by InventoriesTable.app
  var size by InventoriesTable.size

  companion object : LongEntityClass<InventoryDao>(InventoriesTable)
}