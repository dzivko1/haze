package io.github.dzivko1.haze

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication() {
  val secret = environment.config.property("jwt.secret").getString()
  val issuer = environment.config.property("jwt.issuer").getString()
  val audience = environment.config.property("jwt.audience").getString()
  val myRealm = environment.config.property("jwt.realm").getString()

  install(Authentication) {
    jwt("auth-jwt") {
      realm = myRealm
      verifier(
        JWT.require(Algorithm.HMAC256(secret))
          .withAudience(audience)
          .withIssuer(issuer)
          .build()
      )
    }
  }
}