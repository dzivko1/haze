package io.github.dzivko1.haze.server.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.CoroutineContext

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