package io.github.dzivko1.haze.exceptions

import io.github.dzivko1.haze.domain.error.model.ErrorResponse
import io.ktor.http.*

class ClientException(
  val errorCode: ErrorCode,
  val httpStatusCode: HttpStatusCode = HttpStatusCode.BadRequest,
  message: String? = null,
) : Exception(message)

fun ClientException.toErrorResponse(): ErrorResponse {
  return ErrorResponse(
    code = errorCode.code,
    description = errorCode.description,
    message = message
  )
}