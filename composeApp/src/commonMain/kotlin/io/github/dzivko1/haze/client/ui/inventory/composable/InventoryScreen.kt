package io.github.dzivko1.haze.client.ui.inventory.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.dzivko1.haze.client.ui.inventory.InventoryUiState
import io.github.dzivko1.haze.client.ui.inventory.ItemSlotUi
import io.github.dzivko1.haze.client.util.ITEM_SLOT_HEIGHT
import io.github.dzivko1.haze.client.util.ITEM_SLOT_WIDTH

@Composable
fun InventoryScreen(
  uiState: InventoryUiState,
  onSlotClick: (index: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier.fillMaxSize()
  ) {
    BoxWithConstraints {
      val availableWidth = maxWidth
      val itemSlotSpacing = 8.dp
      val maxInventoryWidth = (ITEM_SLOT_WIDTH + itemSlotSpacing) * 10 + itemSlotSpacing

      LazyVerticalGrid(
        columns = GridCells.FixedSize(ITEM_SLOT_WIDTH),
        horizontalArrangement = Arrangement.spacedBy(itemSlotSpacing, alignment = Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(itemSlotSpacing),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 60.dp),
        modifier = Modifier.widthIn(max = maxInventoryWidth)
      ) {
        itemsIndexed(uiState.itemSlots) { index, slot ->
          ItemSlot(
            slot = slot,
            onClick = { onSlotClick(index) },
            modifier = if (availableWidth >= maxInventoryWidth && index % 50 == 49) {
              Modifier.padding(bottom = 50.dp)
            } else Modifier
          )
        }
      }
    }
  }
}

@Composable
private fun ItemSlot(
  slot: ItemSlotUi,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    onClick = onClick,
    shape = MaterialTheme.shapes.medium,
    color = MaterialTheme.colorScheme.surfaceContainer,
    shadowElevation = 2.dp,
    modifier = modifier.size(ITEM_SLOT_WIDTH, ITEM_SLOT_HEIGHT),
  ) {
    if (slot.item != null) {
      AsyncImage(
        model = slot.item.imageUrl,
        contentDescription = slot.item.name,
        filterQuality = FilterQuality.High
      )
    }
  }
}