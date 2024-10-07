package io.github.dzivko1.haze.data

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureContentNegotiation() {
  install(ContentNegotiation) {
    json(
      Json {
        encodeDefaults = false
      }
    )
  }
}