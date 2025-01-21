package io.github.dzivko1.haze.client.ui

import androidx.lifecycle.ViewModel
import io.github.dzivko1.haze.client.domain.user.UserRepository

class MainViewModel(
  private val userRepository: UserRepository
) : ViewModel() {

  /**
   * Temporary quick auto login
   */
  suspend fun autoLogin() {
    if (!userRepository.isUserLoggedIn()) {
      userRepository.authenticateUser("uname", "pass")
    }
  }
}