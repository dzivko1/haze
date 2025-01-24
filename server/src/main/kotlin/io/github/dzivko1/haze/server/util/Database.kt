package io.github.dzivko1.haze.server.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

suspend fun <T> suspendTransaction(
  context: CoroutineContext? = Dispatchers.IO,
  db: Database? = null,
  transactionIsolation: Int? = null,
  statement: suspend Transaction.() -> T,
): T = newSuspendedTransaction(
  context = context,
  db = db,
  transactionIsolation = transactionIsolation,
  statement = statement
)

fun IntIdTable.containsId(id: Int): Boolean {
  return selectAll().where { this@containsId.id eq id }.any()
}

fun LongIdTable.containsId(id: Long): Boolean {
  return selectAll().where { this@containsId.id eq id }.any()
}

fun UUIDTable.containsId(id: UUID): Boolean {
  return selectAll().where { this@containsId.id eq id }.any()
}

fun UUIDTable.containsId(id: Uuid): Boolean {
  return selectAll().where { this@containsId.id eq id.toJavaUuid() }.any()
}