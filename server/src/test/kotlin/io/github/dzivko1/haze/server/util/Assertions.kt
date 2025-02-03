package io.github.dzivko1.haze.server.util

import io.github.dzivko1.haze.domain.error.model.ErrorResponse
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import kotlin.test.assertEquals

fun assertErrorCode(expected: ErrorCode, responseBody: ErrorResponse) {
  assertEquals(expected.code, responseBody.error.code, "Error code is not as expected")
}