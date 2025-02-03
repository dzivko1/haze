package io.github.dzivko1.haze.domain.error.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
  val error: Content
) {
  @Serializable
  data class Content(
    val code: Int,
    val description: String,
    val message: String? = null
  )
}