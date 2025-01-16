package io.github.dzivko1.haze.server.data.item.model

import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppDao
import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object ItemClassesTable : LongIdTable("item_classes") {
  val app = reference("app", HazeAppsTable)
  val name = varchar("name", 50)
  val smallImageUrl = varchar("small_image_url", 500)
  val largeImageUrl = varchar("large_image_url", 500)
}

class ItemClassDao(id: EntityID<Long>) : LongEntity(id) {
  var app by HazeAppDao referencedOn ItemClassesTable.app
  var name by ItemClassesTable.name
  var smallImageUrl by ItemClassesTable.smallImageUrl
  var largeImageUrl by ItemClassesTable.largeImageUrl

  companion object : LongEntityClass<ItemClassDao>(ItemClassesTable)
}