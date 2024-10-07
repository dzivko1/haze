package io.github.dzivko1.haze.exceptions

enum class ErrorCode(
  val code: Int,
  val description: String,
) {
  UsernameTaken(10, "Username taken")
}