package io.github.dzivko1.haze.server.data.user.model

import io.github.dzivko1.haze.domain.user.model.User
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*
import kotlin.uuid.toKotlinUuid

object UsersTable : UUIDTable("users") {
  val name = varchar("name", 32).uniqueIndex()
  val passwordHash = binary("pwdHash", 64)
  val passwordSalt = binary("pwdSalt", 32)
}

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
  var name by UsersTable.name
  var passwordHash by UsersTable.passwordHash
  var passwordSalt by UsersTable.passwordSalt

  companion object : UUIDEntityClass<UserDao>(UsersTable) {
    fun findByName(name: String): UserDao? {
      return UserDao.find { UsersTable.name eq name }.firstOrNull()
    }

    fun nameExists(name: String): Boolean {
      return UsersTable.select(UsersTable.name)
        .where { UsersTable.name eq name }
        .any()
    }
  }
}

fun UserDao.toDomainModel(): User {
  return User(
    id = id.value.toKotlinUuid(),
    name = name
  )
}