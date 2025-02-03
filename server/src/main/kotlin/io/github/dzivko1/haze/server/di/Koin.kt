package io.github.dzivko1.haze.server.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.koinModule() {
  install(Koin) {
    modules(DataModule)
  }
}