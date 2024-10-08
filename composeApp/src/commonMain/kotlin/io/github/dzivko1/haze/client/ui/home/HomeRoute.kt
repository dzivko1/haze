package io.github.dzivko1.haze.client.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeRoute() {
  composable<HomeRoute> {

  }
}