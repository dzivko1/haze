package io.github.dzivko1.haze.data.user

import io.github.dzivko1.haze.data.user.model.AccountDao
import io.github.dzivko1.haze.domain.user.UserRepository
import io.github.dzivko1.haze.exceptions.ClientException
import io.github.dzivko1.haze.exceptions.ErrorCode
import io.github.dzivko1.haze.util.sha256
import io.github.dzivko1.haze.util.suspendTransaction
import io.ktor.util.*

class DbUserRepository : UserRepository {

  override suspend fun saveUser(username: String, password: String) {
    suspendTransaction {
      if (AccountDao.nameExists(username)) throw ClientException(ErrorCode.UsernameTaken)
      val salt = generateNonce(32)
      AccountDao.new {
        this.name = username
        this.passwordHash = sha256(password.toByteArray() + salt)
        this.passwordSalt = salt
      }
    }
  }

  override suspend fun verifyPassword(username: String, password: String): Boolean {
    val user = suspendTransaction { AccountDao.findByName(username) } ?: return false
    val passwordHash = sha256(password.toByteArray() + user.passwordSalt)
    return passwordHash.contentEquals(user.passwordHash)
  }
}