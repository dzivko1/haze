package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.exceptions.ClientException
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.getUserId
import io.github.dzivko1.haze.server.util.getUserIdParam
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.inventoryRoutes() {
  getInventoryRoute()
}

fun Route.getInventoryRoute() {
  val itemRepository by inject<ItemRepository>()

  get("/user/inventory/{appId}/{contextId}") {
    val userId = call.getUserId()
    val appId = call.parameters["appId"]!!.toInt()
    val contextId = call.parameters["contextId"]!!.toInt()

    val inventory = itemRepository.getInventory(inventoryId)
      ?: throw ClientException(ErrorCode.InventoryNotFound)

    call.respond(inventory)
  }

  get("/user/{id}/inventory/{appId}/{contextId}") {
    val userId = call.getUserIdParam("id")
      ?: throw ClientException(ErrorCode.UserNotFound)
    val appId = call.parameters["appId"]!!.toInt()
    val contextId = call.parameters["contextId"]!!.toInt()

    val inventory = itemRepository.getInventory(userId, appId, contextId)
      ?: throw ClientException(ErrorCode.InventoryNotFound)

    call.respond(inventory)
  }
}