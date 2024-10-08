package io.github.dzivko1.haze.server.data.user.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object AccountsTable : UUIDTable("accounts") {
  val name = varchar("name", 32).uniqueIndex()
  val passwordHash = binary("pwdHash", 64)
  val passwordSalt = binary("pwdSalt", 32)
}

class AccountDao(id: EntityID<UUID>) : UUIDEntity(id) {
  var name by AccountsTable.name
  var passwordHash by AccountsTable.passwordHash
  var passwordSalt by AccountsTable.passwordSalt

  companion object : UUIDEntityClass<AccountDao>(AccountsTable) {
    fun findByName(name: String): AccountDao? {
      return AccountDao.find { AccountsTable.name eq name }.firstOrNull()
    }

    fun nameExists(name: String): Boolean {
      return AccountsTable.select(AccountsTable.name)
        .where { AccountsTable.name eq name }
        .any()
    }
  }
}