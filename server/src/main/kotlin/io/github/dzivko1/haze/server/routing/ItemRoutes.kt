package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.DefineItemsRequest
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.itemRoutes() {
  routing {
    authenticate {
      getItemDefinitionRoute()
      defineItemsRoute()
      createItemsRoute()
    }
  }
}

fun Route.getItemDefinitionRoute() {
  val itemRepository by inject<ItemRepository>()

  get("/items/definition/{appId}") {
    val appId = call.parameters["appId"]!!.toInt()
    val itemDef = itemRepository.getItemDefinition(appId)
    call.respond(hashMapOf("items" to itemDef))
  }
}

fun Route.defineItemsRoute() {
  val itemRepository by inject<ItemRepository>()

  post("/items/definition") { body: DefineItemsRequest ->
    val ids = itemRepository.defineItems(body.appId, body.items)
    call.respond(hashMapOf("itemClassIds" to ids))
  }
}

fun Route.createItemsRoute() {
  val itemRepository by inject<ItemRepository>()

  post("/items") { body: CreateItemsRequest ->
    val ids = itemRepository.createItems(body.items)
    call.respond(hashMapOf("itemIds" to ids))
  }
}