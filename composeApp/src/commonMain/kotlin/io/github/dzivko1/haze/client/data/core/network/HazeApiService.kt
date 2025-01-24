package io.github.dzivko1.haze.client.data.core.network

import io.github.aakira.napier.Napier
import io.github.dzivko1.haze.client.data.core.storage.DataStore
import io.github.dzivko1.haze.data.item.model.GetItemDefinitionResponse
import io.github.dzivko1.haze.data.user.inventory.model.SwapItemsRequest
import io.github.dzivko1.haze.data.user.model.UserAuthRequest
import io.github.dzivko1.haze.data.user.model.UserAuthResponse
import io.github.dzivko1.haze.domain.inventory.model.Inventory
import io.github.dzivko1.haze.domain.item.model.ItemClass
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class HazeApiService(
  private val client: HttpClient,
  private val dataStore: DataStore
) {
  suspend fun authenticateUser(username: String, password: String): Result<Unit> {
    return safeCall {
      val token = client.post("auth/login") {
        setBody(UserAuthRequest(username, password))
      }.bodyOrNull<UserAuthResponse>()?.token

      dataStore.authToken = token
    }
  }

  suspend fun getItemDefinition(appId: Int): Result<List<ItemClass>> {
    return safeCall {
      client.get("items/definition/$appId").body<GetItemDefinitionResponse>().items
    }
  }

  suspend fun getInventory(appId: Int): Result<Inventory> {
    return safeCall {
      client.get("user/inventory/$appId").body()
    }
  }

  suspend fun swapItems(inventoryId: Long, indexA: Int, indexB: Int): Result<Unit> {
    return safeCall {
      client.post("inventory/$inventoryId/swap") {
        setBody(SwapItemsRequest(indexA, indexB))
      }
    }
  }

  private suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? {
    return try {
      body()
    } catch (_: Throwable) {
      null
    }
  }

  private inline fun <T> safeCall(call: () -> T): Result<T> {
    return runCatching { call() }
      .onFailure { Napier.e("API service error", it) }
  }
}