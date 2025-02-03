package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.user.model.LoginResponse
import io.github.dzivko1.haze.data.user.model.UserAuthRequest
import io.github.dzivko1.haze.server.domain.user.UserRepository
import io.github.dzivko1.haze.server.generateAuthToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.Instant

fun Application.authRoutes() {
  routing {
    registerRoute()
    loginRoute()
  }
}

private fun Route.registerRoute() {
  val userRepository by inject<UserRepository>()

  post("/auth/register") { body: UserAuthRequest ->
    userRepository.saveUser(body.username, body.password)
    call.respond(HttpStatusCode.Created)
  }
}

private fun Route.loginRoute() {
  val userRepository by inject<UserRepository>()

  post("/auth/login") { body: UserAuthRequest ->
    if (userRepository.verifyPassword(body.username, body.password)) {
      val config = application.environment.config
      val secret = config.property("jwt.secret").getString()
      val issuer = config.property("jwt.issuer").getString()
      val audience = config.property("jwt.audience").getString()
      val userId = userRepository.getUserByName(body.username)!!.id.toHexString()
      val weekFromNow = Instant.now().plusSeconds(7 * 24 * 60 * 60)
      val token = generateAuthToken(userId, secret, issuer, audience, weekFromNow)
      call.respond(LoginResponse(token))
    } else {
      call.respond(HttpStatusCode.Unauthorized)
    }
  }
}