package io.github.dzivko1.haze.server.data

import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import io.github.dzivko1.haze.server.data.item.model.InventoriesTable
import io.github.dzivko1.haze.server.data.item.model.ItemClassesTable
import io.github.dzivko1.haze.server.data.item.model.ItemsTable
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.databaseModule() {
  Database.connect(
    "jdbc:postgresql://localhost:5432/postgres",
    user = "postgres",
    password = "postgres"
  )

  transaction {
    SchemaUtils.createMissingTablesAndColumns(UsersTable, HazeAppsTable, InventoriesTable, ItemClassesTable, ItemsTable)
  }
}