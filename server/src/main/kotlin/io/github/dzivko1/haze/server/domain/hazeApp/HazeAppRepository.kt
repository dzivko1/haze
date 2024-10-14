package io.github.dzivko1.haze.server.domain.hazeApp

interface HazeAppRepository {

  /**
   * Creates a new Haze app.
   *
   * @return The ID of the app.
   */
  suspend fun registerApp(name: String): Int
}