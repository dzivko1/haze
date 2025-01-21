package io.github.dzivko1.haze.client.di

import io.github.dzivko1.haze.client.ui.MainViewModel
import io.github.dzivko1.haze.client.ui.inventory.InventoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val UiModule = module {
  viewModelOf(::MainViewModel)
  viewModelOf(::InventoryViewModel)
}