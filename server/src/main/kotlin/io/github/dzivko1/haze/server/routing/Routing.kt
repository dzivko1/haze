package io.github.dzivko1.haze.server.routing

import io.ktor.server.application.*

fun Application.configureRouting() {
  authRoutes()
  appRoutes()
}