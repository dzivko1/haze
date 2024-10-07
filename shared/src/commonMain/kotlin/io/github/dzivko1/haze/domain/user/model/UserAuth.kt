package io.github.dzivko1.haze.domain.user.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuth(
  val username: String,
  val password: String,
)
