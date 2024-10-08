package io.github.dzivko1.haze.client.ui.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ProfileRoute

fun NavGraphBuilder.profileRoute() {
  composable<ProfileRoute> {

  }
}