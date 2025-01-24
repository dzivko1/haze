package io.github.dzivko1.haze.client.domain.item

import io.github.dzivko1.haze.client.data.core.network.HazeApiService
import io.github.dzivko1.haze.domain.inventory.model.Inventory
import io.github.dzivko1.haze.domain.item.model.ItemClass
import kotlinx.coroutines.flow.*

class ItemRepository(
  private val api: HazeApiService
) {
  private val _inventoryFlow = MutableStateFlow<Inventory?>(null)
  private val inventoryFlow = _inventoryFlow.asStateFlow()

  private val itemDefinitions = mutableMapOf<Int, List<ItemClass>>()

  suspend fun getItemDefinition(appId: Int): Result<List<ItemClass>> {
    return itemDefinitions[appId]?.let { Result.success(it) }
      ?: api.getItemDefinition(appId)
        .onSuccess { itemDefinitions[appId] = it }
  }

  fun getInventoryFlow(appId: Int): Flow<Inventory> {
    return inventoryFlow.onSubscription {
      if (inventoryFlow.value == null) fetchInventory(appId)
    }.filterNotNull()
  }

  suspend fun fetchInventory(appId: Int): Result<Inventory> {
    return api.getInventory(appId)
      .onSuccess { _inventoryFlow.value = it }
  }

  suspend fun swapItems(indexA: Int, indexB: Int): Result<Unit> {
    return api.swapItems(inventoryFlow.value!!.id, indexA, indexB)
  }
}