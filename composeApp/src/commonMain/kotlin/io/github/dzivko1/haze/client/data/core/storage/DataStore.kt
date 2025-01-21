package io.github.dzivko1.haze.client.data.core.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.nullableString

class DataStore(
  private val settings: Settings
) {
  var authToken: String? by settings.nullableString("authToken")
}