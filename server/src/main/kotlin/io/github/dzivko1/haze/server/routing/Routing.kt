package io.github.dzivko1.haze.server.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.routingModule() {
  install(IgnoreTrailingSlash)

  authRoutes()
  appRoutes()
  userRoutes()
  inventoryRoutes()
  itemRoutes()
}