package io.github.dzivko1.haze.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlin.uuid.Uuid

fun Application.configureAuthentication() {
  val secret = environment.config.property("jwt.secret").getString()
  val issuer = environment.config.property("jwt.issuer").getString()
  val audience = environment.config.property("jwt.audience").getString()
  val myRealm = environment.config.property("jwt.realm").getString()

  install(Authentication) {
    jwt {
      realm = myRealm
      verifier(
        JWT.require(Algorithm.HMAC256(secret))
          .withAudience(audience)
          .withIssuer(issuer)
          .build()
      )

      validate { credential ->
        if (credential.getUserId() != null) {
          JWTPrincipal(credential.payload)
        } else {
          null
        }
      }

      challenge { _, _ ->
        call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
      }
    }
  }
}

fun JWTCredential.getUserId(): Uuid? {
  return payload.getUserId()
}

fun JWTPrincipal.getUserId(): Uuid? {
  return payload.getUserId()
}

fun Payload.getUserId(): Uuid? {
  return kotlin.runCatching {
    Uuid.parseHex(getClaim("userId").asString())
  }.getOrNull()
}