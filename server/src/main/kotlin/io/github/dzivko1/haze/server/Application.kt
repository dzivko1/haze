package io.github.dzivko1.haze.server

import io.github.aakira.napier.Napier
import io.github.dzivko1.haze.server.data.contentNegotiationModule
import io.github.dzivko1.haze.server.data.databaseModule
import io.github.dzivko1.haze.server.di.koinModule
import io.github.dzivko1.haze.server.exceptions.statusPagesModule
import io.github.dzivko1.haze.server.logging.KtorAntilog
import io.github.dzivko1.haze.server.routing.routingModule
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
  Napier.base(KtorAntilog())
  koinModule()
  databaseModule()
  contentNegotiationModule()
  authenticationModule()
  statusPagesModule()
  routingModule()
}
