package io.github.dzivko1.haze.client.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object HomeSectionRoute

fun NavGraphBuilder.homeSection() {
  navigation<HomeSectionRoute>(startDestination = HomeRoute) {
    homeRoute()
  }
}