package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.hazeApp.model.RegisterAppRequest
import io.github.dzivko1.haze.server.domain.hazeApp.HazeAppRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.appRoutes() {
  routing {
    authenticate {
      registerAppRoute()
    }
  }
}

fun Route.registerAppRoute() {
  val appRepository by inject<HazeAppRepository>()

  post("/apps/register") {
    val request = call.receive<RegisterAppRequest>()
    val appId = appRepository.registerApp(request.name)
    call.respond(hashMapOf("appId" to appId))
  }
}