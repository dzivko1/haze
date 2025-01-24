package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.user.inventory.model.SwapItemsRequest
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.inventoryRoutes() {
  routing {
    swapItemsRoute()
  }
}

private fun Route.swapItemsRoute() {
  val itemRepository by inject<ItemRepository>()

  post("/inventory/{inventoryId}/swap") { body: SwapItemsRequest ->
    val inventoryId = call.parameters["inventoryId"]!!.toLong()

    itemRepository.swapItems(inventoryId, body.indexA, body.indexB)

    call.respond(HttpStatusCode.OK)
  }
}