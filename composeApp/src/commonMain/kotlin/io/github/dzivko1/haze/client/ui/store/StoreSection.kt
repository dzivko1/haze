package io.github.dzivko1.haze.client.ui.store

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object StoreSectionRoute

fun NavGraphBuilder.storeSection() {
  navigation<StoreSectionRoute>(startDestination = StoreRoute) {
    storeRoute()
  }
}