package io.github.dzivko1.haze.domain.user.model

import io.github.dzivko1.haze.data.UuidSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class User(
  @Serializable(with = UuidSerializer::class)
  val id: Uuid,
  val name: String,
)