package io.github.dzivko1.haze.server.data

import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import io.github.dzivko1.haze.server.data.item.model.ItemClassesTable
import io.github.dzivko1.haze.server.data.item.model.ItemsTable
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
  Database.connect(
    "jdbc:postgresql://localhost:5432/postgres",
    user = "postgres"
  )

  transaction {
    SchemaUtils.create(UsersTable, HazeAppsTable, ItemsTable, ItemClassesTable)
  }
}