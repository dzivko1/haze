package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.server.domain.user.UserRepository
import io.github.dzivko1.haze.server.getUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Application.userRoutes() {
  routing {
    authenticate {
      getUserRoute()
    }
  }
}

fun Route.getUserRoute() {
  val userRepository by inject<UserRepository>()

  get("/user/") {
    val id = call.principal<JWTPrincipal>()?.getUserId()
      ?: throw Exception("Could not determine requesting user.")

    val user = userRepository.getUser(id)
      ?: throw Exception("Could not retrieve user from database (id = $id).")

    call.respond(user)
  }

  get("/user/{id}") {
    val id = call.parameters["id"]
      ?.runCatching { Uuid.parseHex(this) }
      ?.getOrElse { call.respond(HttpStatusCode.NotFound); return@get }
      ?: throw Exception("Could not determine requesting user.")

    val user = userRepository.getUser(id)
      ?: throw Exception("Could not retrieve user from database (id = $id).")

    call.respond(user)
  }
}