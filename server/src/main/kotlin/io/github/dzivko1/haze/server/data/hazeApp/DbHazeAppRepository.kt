package io.github.dzivko1.haze.server.data.hazeApp

import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppDao
import io.github.dzivko1.haze.server.domain.hazeApp.HazeAppRepository
import io.github.dzivko1.haze.server.util.suspendTransaction

class DbHazeAppRepository : HazeAppRepository {

  override suspend fun registerApp(name: String): Int {
    return suspendTransaction {
      HazeAppDao.new {
        this.name = name
      }.id.value
    }
  }
}