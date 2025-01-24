package io.github.dzivko1.haze.client.ui.inventory.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.dzivko1.haze.client.ui.inventory.InventoryUiState
import io.github.dzivko1.haze.client.ui.inventory.ItemSlotUi
import io.github.dzivko1.haze.client.ui.inventory.ItemUi
import io.github.dzivko1.haze.client.util.ITEM_SLOT_HEIGHT
import io.github.dzivko1.haze.client.util.ITEM_SLOT_WIDTH

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryScreen(
  uiState: InventoryUiState,
  onSlotClick: (index: Int) -> Unit,
  onItemSwap: (indexA: Int, indexB: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val slotSizePx = with(LocalDensity.current) { Size(ITEM_SLOT_WIDTH.toPx(), ITEM_SLOT_HEIGHT.toPx()) }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier.fillMaxSize()
  ) {
    BoxWithConstraints {
      val availableWidth = maxWidth
      val itemSlotSpacing = 8.dp
      val maxInventoryWidth = (ITEM_SLOT_WIDTH + itemSlotSpacing) * 10 + itemSlotSpacing

      var draggedItem by remember { mutableStateOf<ItemUi?>(null) }
      var draggedItemCenter by remember { mutableStateOf(Offset.Zero) }

      val slotPositions = remember { mutableStateMapOf<Int, Offset>() }

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
            modifier = Modifier.then(
              // Separate pages when at full width
              if (availableWidth >= maxInventoryWidth && index % 50 == 49) {
                Modifier.padding(bottom = 50.dp)
              } else Modifier
            ).onGloballyPositioned {
              slotPositions[index] = it.positionInParent()
            }.draggable2D(
              state = rememberDraggable2DState { draggedItemCenter += it },
              onDragStarted = { anchorOffset ->
                val slotPosition = slotPositions[index] ?: Offset.Unspecified
                draggedItemCenter = slotPosition + anchorOffset
                draggedItem = slot.item
              },
              onDragStopped = {
                val targetIndex = slotPositions.entries.find { (_, slotPosition) ->
                  draggedItemCenter.x in (slotPosition.x..slotPosition.x + slotSizePx.width)
                    && draggedItemCenter.y in slotPosition.y..slotPosition.y + slotSizePx.height
                }?.key

                if (targetIndex != null && targetIndex != index) {
                  onItemSwap(index, targetIndex)
                }
                draggedItem = null
              }
            )
          )
        }
      }

      draggedItem?.let {
        AsyncImage(
          model = it.imageUrl,
          contentDescription = it.name,
          filterQuality = FilterQuality.High,
          modifier = Modifier
            .size(ITEM_SLOT_WIDTH, ITEM_SLOT_HEIGHT)
            .offset {
              // Center item on pointer
              IntOffset(
                x = (draggedItemCenter.x - slotSizePx.width / 2).toInt(),
                y = (draggedItemCenter.y - slotSizePx.height / 2).toInt()
              )
            }
        )
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