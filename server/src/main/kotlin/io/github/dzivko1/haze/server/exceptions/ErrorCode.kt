package io.github.dzivko1.haze.server.exceptions

enum class ErrorCode(
  val code: Int,
  val description: String,
) {
  UsernameTaken(10, "Username taken"),
  UserNotFound(11, "User not found"),
  InventoryNotFound(12, "Inventory not found")
}