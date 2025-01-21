package io.github.dzivko1.haze.client.di

import com.russhwolf.settings.Settings
import io.github.dzivko1.haze.client.data.core.network.HazeApiService
import io.github.dzivko1.haze.client.data.core.storage.DataStore
import io.github.dzivko1.haze.client.domain.item.ItemRepository
import io.github.dzivko1.haze.client.domain.user.UserRepository
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DataModule = module {
  factory {
    HttpClient {
      install(Logging) {
        level = LogLevel.ALL
      }
      install(ContentNegotiation) {
        json(
          Json {
            encodeDefaults = false
          }
        )
      }

      defaultRequest {
        url("http://localhost:8080/")
        contentType(ContentType.Application.Json)

        val dataStore = get<DataStore>()
        dataStore.authToken?.let { token -> bearerAuth(token) }
      }
    }
  }
  singleOf(::Settings)
  singleOf(::DataStore)
  singleOf(::HazeApiService)
  singleOf(::UserRepository)
  singleOf(::ItemRepository)
}