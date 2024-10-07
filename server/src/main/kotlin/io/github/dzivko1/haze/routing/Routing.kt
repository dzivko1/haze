package io.github.dzivko1.haze.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
  routing {
    authRoutes()
  }
}