package io.github.dzivko1.haze.server.data.hazeApp.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object HazeAppsTable : IntIdTable("haze_apps") {
  val name = varchar("name", 50)
}

class HazeAppDao(id: EntityID<Int>) : IntEntity(id) {
  var name by HazeAppsTable.name

  companion object : IntEntityClass<HazeAppDao>(HazeAppsTable)
}