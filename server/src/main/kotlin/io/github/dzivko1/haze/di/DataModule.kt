package io.github.dzivko1.haze.di

import io.github.dzivko1.haze.data.user.DbUserRepository
import io.github.dzivko1.haze.domain.user.UserRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DataModule = module {
  singleOf(::DbUserRepository) { bind<UserRepository>() }
}