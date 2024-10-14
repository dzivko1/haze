package io.github.dzivko1.haze.server.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
  install(IgnoreTrailingSlash)

  authRoutes()
  appRoutes()
  userRoutes()
}