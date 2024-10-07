package io.github.dzivko1.haze

import io.github.dzivko1.haze.data.configureContentNegotiation
import io.github.dzivko1.haze.data.configureDatabase
import io.github.dzivko1.haze.di.configureKoin
import io.github.dzivko1.haze.exceptions.configureStatusPages
import io.github.dzivko1.haze.routing.configureRouting
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
