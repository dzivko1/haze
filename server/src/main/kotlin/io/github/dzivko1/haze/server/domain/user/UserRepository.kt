package io.github.dzivko1.haze.server.domain.user

import io.github.dzivko1.haze.domain.user.model.User
import kotlin.uuid.Uuid

interface UserRepository {

  suspend fun getUser(id: Uuid): User?

  suspend fun getUserByName(username: String): User?

  suspend fun saveUser(username: String, password: String)

  suspend fun verifyPassword(username: String, password: String): Boolean
}