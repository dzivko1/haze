package io.github.dzivko1.haze.server.util

import io.github.dzivko1.haze.server.BaseTestBuilder
import io.github.dzivko1.haze.server.data.user.model.UserDao
import io.github.dzivko1.haze.server.generateAuthToken
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import java.time.Instant
import kotlin.uuid.toKotlinUuid

fun BaseTestBuilder.loginUser(user: UserDao) {
  val userIdClaim = user.id.value.toKotlinUuid().toHexString()
  val secret = config.property("jwt.secret").getString()
  val issuer = config.property("jwt.issuer").getString()
  val audience = config.property("jwt.audience").getString()
  val weekFromNow = Instant.now().plusSeconds(7 * 24 * 60 * 60)
  val token = generateAuthToken(userIdClaim, secret, issuer, audience, weekFromNow)

  client = client.config {
    defaultRequest {
      bearerAuth(token)
    }
  }
}