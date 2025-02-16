package io.github.dzivko1.haze.server.di

import io.github.dzivko1.haze.server.data.hazeApp.DbHazeAppRepository
import io.github.dzivko1.haze.server.data.item.DbItemRepository
import io.github.dzivko1.haze.server.data.user.DbUserRepository
import io.github.dzivko1.haze.server.domain.hazeApp.HazeAppRepository
import io.github.dzivko1.haze.server.domain.item.ItemRepository
import io.github.dzivko1.haze.server.domain.user.UserRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DataModule = module {
  singleOf(::DbUserRepository) { bind<UserRepository>() }
  singleOf(::DbHazeAppRepository) { bind<HazeAppRepository>() }
  singleOf(::DbItemRepository) { bind<ItemRepository>() }
}