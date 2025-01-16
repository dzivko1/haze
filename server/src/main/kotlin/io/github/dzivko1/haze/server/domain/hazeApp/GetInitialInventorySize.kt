package io.github.dzivko1.haze.server.domain.hazeApp

fun getInitialInventorySize(appId: Int): Int {
  return when (appId) {
    1 -> 50
    else -> 50
  }
}