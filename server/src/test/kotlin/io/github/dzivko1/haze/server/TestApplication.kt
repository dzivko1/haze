package io.github.dzivko1.haze.server

import io.github.aakira.napier.Napier
import io.github.dzivko1.haze.server.data.contentNegotiationModule
import io.github.dzivko1.haze.server.di.koinModule
import io.github.dzivko1.haze.server.exceptions.statusPagesModule
import io.github.dzivko1.haze.server.logging.KtorAntilog
import io.github.dzivko1.haze.server.routing.routingModule
import io.ktor.server.application.*

@Suppress("unused")
fun Application.testModule() {
  Napier.base(KtorAntilog())
  koinModule()
  contentNegotiationModule()
  authenticationModule()
  statusPagesModule()
  routingModule()
}