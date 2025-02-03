package io.github.dzivko1.haze.server

import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json

class BaseTestBuilder(
  private val applicationTestBuilder: ApplicationTestBuilder
) : ClientProvider {

  private var _client: HttpClient? = null
  override var client: HttpClient
    get() = _client ?: createDefaultClient().also { _client = it }
    set(value) {
      _client = value
    }

  val config by lazy { ApplicationConfig("application.conf") }

  override fun createClient(block: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit): HttpClient =
    applicationTestBuilder.createClient(block)

  fun createDefaultClient(): HttpClient = createClient {
    install(Logging) {
      logger = object: Logger {
        override fun log(message: String) {
          Napier.v("HTTP Client", null, message)
        }
      }
      level = LogLevel.ALL
    }

    install(ContentNegotiation) {
      json(
        Json {
          encodeDefaults = false
          ignoreUnknownKeys = true
        }
      )
    }

    defaultRequest {
      contentType(ContentType.Application.Json)
    }
  }
}
