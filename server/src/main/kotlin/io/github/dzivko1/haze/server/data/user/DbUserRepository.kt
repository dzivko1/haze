package io.github.dzivko1.haze.server.data.user

import io.github.dzivko1.haze.domain.user.model.User
import io.github.dzivko1.haze.server.data.user.model.UserDao
import io.github.dzivko1.haze.server.data.user.model.toDomainModel
import io.github.dzivko1.haze.server.domain.user.UserRepository
import io.github.dzivko1.haze.server.exceptions.ClientException
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.sha256
import io.github.dzivko1.haze.server.util.suspendTransaction
import io.ktor.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class DbUserRepository : UserRepository {

  override suspend fun getUser(id: Uuid): User? {
    return suspendTransaction {
      UserDao.findById(id.toJavaUuid())?.toDomainModel()
    }
  }

  override suspend fun getUserByName(username: String): User? {
    return suspendTransaction {
      UserDao.findByName(username)?.toDomainModel()
    }
  }

  override suspend fun saveUser(username: String, password: String) {
    suspendTransaction {
      if (UserDao.nameExists(username)) throw ClientException(ErrorCode.UsernameTaken)
      val salt = generateNonce(32)
      UserDao.new {
        this.name = username
        this.passwordHash = sha256(password.toByteArray() + salt)
        this.passwordSalt = salt
      }
    }
  }

  override suspend fun verifyPassword(username: String, password: String): Boolean {
    val user = suspendTransaction { UserDao.findByName(username) } ?: return false
    val passwordHash = sha256(password.toByteArray() + user.passwordSalt)
    return passwordHash.contentEquals(user.passwordHash)
  }
}