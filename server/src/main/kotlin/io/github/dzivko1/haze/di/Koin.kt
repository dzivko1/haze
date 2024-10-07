package io.github.dzivko1.haze.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
  install(Koin) {
    modules(DataModule)
  }
}