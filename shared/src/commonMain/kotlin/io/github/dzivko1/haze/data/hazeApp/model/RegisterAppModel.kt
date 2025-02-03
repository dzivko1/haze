package io.github.dzivko1.haze.data.hazeApp.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterAppRequest(
  val name: String,
)

@Serializable
data class RegisterAppResponse(
  val appId: Int
)