package io.github.dzivko1.haze.domain.user

interface UserRepository {

  suspend fun saveUser(username: String, password: String)

  suspend fun verifyPassword(username: String, password: String): Boolean
}