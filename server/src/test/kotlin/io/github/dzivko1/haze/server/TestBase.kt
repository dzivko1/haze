package io.github.dzivko1.haze.server

import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass

abstract class TestBase(
  private val table: Table,
  private vararg val tables: Table,
) {
  fun baseTestApp(block: suspend BaseTestBuilder.() -> Unit) = testApplication {
    BaseTestBuilder(this).apply {
      block()
    }
  }

  @Before
  fun setUpTables() {
    transaction {
      SchemaUtils.create(table, *tables)
    }
  }

  @After
  fun tearDownTables() {
    transaction {
      SchemaUtils.drop(table, *tables)
    }
  }

  companion object {
    protected lateinit var database: Database

    @JvmStatic
    @BeforeClass
    fun setUpDatabase() {
      database = Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1")
    }

    @JvmStatic
    @AfterClass
    fun tearDownDatabase() = TransactionManager.closeAndUnregister(database)
  }

}