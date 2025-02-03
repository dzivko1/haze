package io.github.dzivko1.haze.data.user.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthRequest(
  val username: String,
  val password: String,
)

@Serializable
data class LoginResponse(
  val token: String
)