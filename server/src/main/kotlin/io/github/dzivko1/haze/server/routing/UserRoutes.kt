package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.domain.user.UserRepository
import io.github.dzivko1.haze.server.exceptions.ClientException
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.getUserId
import io.github.dzivko1.haze.server.util.getUserIdParam
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.userRoutes() {
  routing {
    authenticate {
      getUserRoute()
      getUserInventoryRoute()
    }
  }
}

private fun Route.getUserRoute() {
  val userRepository by inject<UserRepository>()

  get("/user/") {
    val userId = call.getUserId()

    val user = userRepository.getUser(userId)
      ?: throw Exception("Could not retrieve user from repository (id = $userId).")

    call.respond(user)
  }

  get("/user/{id}") {
    val userId = call.getUserIdParam("id")
      ?: throw ClientException(ErrorCode.UserNotFound)

    val user = userRepository.getUser(userId)
      ?: throw ClientException(ErrorCode.UserNotFound)

    call.respond(user)
  }
}

private fun Route.getUserInventoryRoute() {
  val itemRepository by inject<ItemRepository>()

  get("/user/inventory/{appId}") {
    val userId = call.getUserId()
    val appId = call.parameters["appId"]!!.toInt()

    val inventory = itemRepository.getInventory(userId, appId)
      ?: throw ClientException(ErrorCode.InventoryNotFound)

    call.respond(inventory)
  }

  get("/user/{userId}/inventory/{appId}") {
    val userId = call.getUserIdParam("userId")
      ?: throw ClientException(ErrorCode.UserNotFound)
    val appId = call.parameters["appId"]!!.toInt()

    val inventory = itemRepository.getInventory(userId, appId)
      ?: throw ClientException(ErrorCode.InventoryNotFound)

    call.respond(inventory)
  }
}