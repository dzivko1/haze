package io.github.dzivko1.haze.server.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.dzivko1.haze.domain.user.model.UserAuth
import io.github.dzivko1.haze.server.domain.user.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.Instant

fun Route.authRoutes() {
  route("/auth") {
    registerRoute()
    loginRoute()
  }
}

fun Route.registerRoute() {
  val userRepository by inject<UserRepository>()

  post("/register") {
    val authData = call.receive<UserAuth>()
    userRepository.saveUser(authData.username, authData.password)
    call.respond(HttpStatusCode.Created)
  }
}

fun Route.loginRoute() {
  val userRepository by inject<UserRepository>()

  post("/login") {
    val authData = call.receive<UserAuth>()

    if (userRepository.verifyPassword(authData.username, authData.password)) {
      val config = application.environment.config
      val secret = config.property("jwt.secret").getString()
      val issuer = config.property("jwt.issuer").getString()
      val audience = config.property("jwt.audience").getString()
      val weekFromNow = Instant.now().plusSeconds(7 * 24 * 60 * 60)
      val token = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("username", authData.username)
        .withExpiresAt(weekFromNow)
        .sign(Algorithm.HMAC256(secret))
      call.respond(hashMapOf("token" to token))
    } else {
      call.respond(HttpStatusCode.Unauthorized)
    }
  }
}