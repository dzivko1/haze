package io.github.dzivko1.haze.server.data

import io.github.dzivko1.haze.server.data.user.model.AccountsTable
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
    //SchemaUtils.drop(AccountsTable)
    SchemaUtils.create(AccountsTable)
  }
}