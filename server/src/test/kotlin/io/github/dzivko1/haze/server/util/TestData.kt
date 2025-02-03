package io.github.dzivko1.haze.server.util

import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppDao
import io.github.dzivko1.haze.server.data.item.model.InventoryDao
import io.github.dzivko1.haze.server.data.item.model.ItemClassDao
import io.github.dzivko1.haze.server.data.item.model.ItemDao
import io.github.dzivko1.haze.server.data.user.model.UserDao
import io.ktor.util.*
import org.jetbrains.exposed.sql.transactions.transaction

fun createTestUser(
  name: String = "uname",
  password: String = "pass"
): UserDao {
  return transaction {
    UserDao.new {
      this.name = name
      val salt = generateNonce(32)
      passwordHash = sha256(password.toByteArray() + salt)
      passwordSalt = salt
    }
  }
}

fun createTestApp(name: String = "Test app"): HazeAppDao {
  return transaction {
    HazeAppDao.new {
      this.name = name
    }
  }
}

fun createTestInventory(
  user: UserDao = createTestUser(),
  app: HazeAppDao = createTestApp(),
  size: Int = 50
): InventoryDao {
  return transaction {
    InventoryDao.new {
      this.user = user
      this.app = app
      this.size = size
    }
  }
}

fun createTestItemClass(
  app: HazeAppDao = createTestApp(),
  name: String = "Test item class",
  smallImageUrl: String = "http://media.steampowered.com/apps/440/icons/key.be0a5e2cda3a039132c35b67319829d785e50352.png",
  largeImageUrl: String = "http://media.steampowered.com/apps/440/icons/key_large.354829243e53d73a5a75323c88fc5689ecb19359.png"
): ItemClassDao {
  return transaction {
    ItemClassDao.new {
      this.app = app
      this.name = name
      this.smallImageUrl = smallImageUrl
      this.largeImageUrl = largeImageUrl
    }
  }
}

fun createTestItem(
  itemClass: ItemClassDao = createTestItemClass(),
  inventory: InventoryDao = createTestInventory(),
  slotIndex: Int? = null
): ItemDao {
  return transaction {
    ItemDao.new {
      this.itemClass = itemClass
      this.inventory = inventory
      this.slotIndex = slotIndex
    }
  }
}