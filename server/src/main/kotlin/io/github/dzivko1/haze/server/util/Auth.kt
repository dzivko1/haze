package io.github.dzivko1.haze.server.util

import com.auth0.jwt.interfaces.Payload
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlin.uuid.Uuid

/**
 * Extracts a valid user ID from the JWT claim.
 * Returns `null` if a valid ID cannot be extracted.
 */
fun JWTCredential.getUserId(): Uuid? {
  return payload.getUserId()
}

/**
 * Extracts a valid user ID from the JWT claim.
 * Returns `null` if a valid ID cannot be extracted.
 */
fun JWTPrincipal.getUserId(): Uuid? {
  return payload.getUserId()
}

/**
 * Extracts a valid user ID from the JWT claim.
 * Returns `null` if a valid ID cannot be extracted.
 */
fun Payload.getUserId(): Uuid? {
  return runCatching {
    getClaim("userId").asString()?.let { Uuid.parseHex(it) }
  }.getOrNull()
}

/**
 * Extracts a valid user ID claim from this call's JWT principal.
 * An exception is thrown if a valid ID cannot be extracted.
 */
fun ApplicationCall.getUserId(): Uuid {
  return principal<JWTPrincipal>()?.getUserId()
    ?: throw Exception("Could not determine requesting user ID.")
}

/**
 * Extracts the user ID from this call's path parameter specified by [paramName].
 * Returns `null` if an ID cannot be extracted.
 */
fun ApplicationCall.getUserIdParam(paramName: String): Uuid? {
  return runCatching {
    parameters[paramName]?.let { Uuid.parseHex(it) }
  }.getOrNull()
}