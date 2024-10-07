package io.github.dzivko1.haze.exceptions

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
  install(StatusPages) {
    exception<ClientException> { call, cause ->
      call.respond(
        message = hashMapOf("error" to cause.toErrorResponse()),
        status = cause.httpStatusCode
      )
    }
  }
}