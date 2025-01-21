package io.github.dzivko1.haze.client.domain.user

import io.github.dzivko1.haze.client.data.core.network.HazeApiService
import io.github.dzivko1.haze.client.data.core.storage.DataStore

class UserRepository(
  private val api: HazeApiService,
  private val dataStore: DataStore
) {
  fun isUserLoggedIn(): Boolean {
    return dataStore.authToken != null
  }

  suspend fun authenticateUser(username: String, password: String) {
    api.authenticateUser(username, password)
  }
}