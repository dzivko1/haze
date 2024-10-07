package io.github.dzivko1.haze.data

import io.github.dzivko1.haze.data.user.model.AccountsTable
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
    SchemaUtils.create(AccountsTable)
  }
}