package io.github.dzivko1.haze.di

import io.github.dzivko1.haze.ui.inventory.InventoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val UiModule = module {
  viewModelOf(::InventoryViewModel)
}