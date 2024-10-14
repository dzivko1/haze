package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.itemRoutes() {
  routing {
    authenticate {
      defineItemsRoute()
      createItemsRoute()
    }
  }
}

fun Route.defineItemsRoute() {
  val itemRepository by inject<ItemRepository>()

  post("/items/definition") {
    val request = call.receive<DefineItemsRequest>()
    val ids = itemRepository.defineItems(request.items)
    call.respond(hashMapOf("itemClassIds" to ids))
  }
}

fun Route.createItemsRoute() {
  val itemRepository by inject<ItemRepository>()

  post("/items") {
    val request = call.receive<CreateItemsRequest>()
    val ids = itemRepository.createItems(request.items)
    call.respond(hashMapOf("itemIds" to ids))
  }
}