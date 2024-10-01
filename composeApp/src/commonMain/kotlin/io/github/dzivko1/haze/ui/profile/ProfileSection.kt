package io.github.dzivko1.haze.ui.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object ProfileSectionRoute

fun NavGraphBuilder.profileSection() {
  navigation<ProfileSectionRoute>(startDestination = ProfileRoute) {
    profileRoute()
  }
}