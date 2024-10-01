package io.github.dzivko1.haze.ui.store

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object StoreRoute

fun NavGraphBuilder.storeRoute() {
  composable<StoreRoute> {

  }
}