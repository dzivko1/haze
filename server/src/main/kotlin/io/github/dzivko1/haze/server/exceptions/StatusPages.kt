package io.github.dzivko1.haze.server.exceptions

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.statusPagesModule() {
  install(StatusPages) {
    exception<ClientException> { call, cause ->
      call.respond(
        message = cause.toErrorResponse(),
        status = cause.httpStatusCode
      )
    }
  }
}