package io.github.dzivko1.haze.server

import io.github.dzivko1.haze.server.data.configureContentNegotiation
import io.github.dzivko1.haze.server.data.configureDatabase
import io.github.dzivko1.haze.server.di.configureKoin
import io.github.dzivko1.haze.server.exceptions.configureStatusPages
import io.github.dzivko1.haze.server.routing.configureRouting
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
  configureKoin()
  configureDatabase()
  configureContentNegotiation()
  configureAuthentication()
  configureStatusPages()
  configureRouting()
}
