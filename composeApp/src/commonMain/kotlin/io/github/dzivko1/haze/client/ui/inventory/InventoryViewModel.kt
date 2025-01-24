package io.github.dzivko1.haze.client.ui.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dzivko1.haze.client.domain.item.ItemRepository
import kotlinx.coroutines.launch

class InventoryViewModel(
  private val itemRepository: ItemRepository
) : ViewModel() {
  var uiState by mutableStateOf(InventoryUiState())
    private set

  init {
    viewModelScope.launch {
      itemRepository.getInventoryFlow(1).collect { inventory ->
        // TODO handle errors
        val itemDefinition = itemRepository.getItemDefinition(1).getOrThrow()
        uiState = uiState.copy(
          isLoading = false,
          itemSlots = List(inventory.size) { index ->
            val item = inventory.items.find { it.slotIndex == index }
            if (item != null) {
              val itemClass = itemDefinition.find { it.id == item.itemClassId }
              ItemSlotUi(
                item = ItemUi(
                  id = item.id,
                  name = itemClass?.name,
                  imageUrl = itemClass?.largeImageUrl
                )
              )
            } else {
              ItemSlotUi(item = null)
            }
          }
        )
      }
    }
  }

  fun swapItems(indexA: Int, indexB: Int) {
    viewModelScope.launch {
      uiState = uiState.copy(
        itemSlots = uiState.itemSlots.mapIndexed { index, slot ->
          if (index == indexA || index == indexB) {
            slot.copy(isLoading = true)
          } else slot
        }
      )
      itemRepository.swapItems(indexA, indexB)
        .onSuccess {
          uiState = uiState.copy(
            itemSlots = uiState.itemSlots.mapIndexed { index, slot ->
              when (index) {
                indexA -> slot.copy(item = uiState.itemSlots[indexB].item, isLoading = false)
                indexB -> slot.copy(item = uiState.itemSlots[indexA].item, isLoading = false)
                else -> slot
              }
            }
          )
        }.onFailure {
          uiState = uiState.copy(
            itemSlots = uiState.itemSlots.mapIndexed { index, slot ->
              if (index == indexA || index == indexB) {
                slot.copy(isLoading = false)
              } else slot
            }
          )
        }
    }
  }
}