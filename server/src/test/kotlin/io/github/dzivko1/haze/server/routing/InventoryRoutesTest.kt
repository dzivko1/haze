package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.inventory.model.SwapItemsRequest
import io.github.dzivko1.haze.server.TestBase
import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import io.github.dzivko1.haze.server.data.item.model.InventoriesTable
import io.github.dzivko1.haze.server.data.item.model.ItemClassesTable
import io.github.dzivko1.haze.server.data.item.model.ItemDao
import io.github.dzivko1.haze.server.data.item.model.ItemsTable
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.assertErrorCode
import io.github.dzivko1.haze.server.util.createTestApp
import io.github.dzivko1.haze.server.util.createTestInventory
import io.github.dzivko1.haze.server.util.createTestItem
import io.github.dzivko1.haze.server.util.createTestItemClass
import io.github.dzivko1.haze.server.util.createTestUser
import io.github.dzivko1.haze.server.util.loginUser
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import kotlin.test.assertEquals

class InventoryRoutesTest : TestBase(InventoriesTable, ItemClassesTable, ItemsTable, HazeAppsTable, UsersTable) {
  @Test
  fun acceptNewItems_success() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val inventory = createTestInventory(user, app)
    val itemClass = createTestItemClass(app)
    val item1 = createTestItem(itemClass, inventory, slotIndex = 0)
    val item2 = createTestItem(itemClass, inventory, slotIndex = 2)
    val item3 = createTestItem(itemClass, inventory, slotIndex = null)
    val item4 = createTestItem(itemClass, inventory, slotIndex = null)

    loginUser(user)

    val response = client.post("inventory/${inventory.id.value}/accept")

    transaction {
      assertEquals(4, ItemDao.count())
      assertEquals(0, item1.slotIndex)
      assertEquals(2, item2.slotIndex)
      assertEquals(1, item3.slotIndex)
      assertEquals(3, item4.slotIndex)
      assertEquals(HttpStatusCode.OK, response.status)
    }
  }

  @Test
  fun acceptNewItems_noNewItems_success() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val inventory = createTestInventory(user, app)
    val itemClass = createTestItemClass(app)
    createTestItem(itemClass, inventory, slotIndex = 0)

    loginUser(user)

    val response = client.post("inventory/${inventory.id.value}/accept")

    transaction {
      val item = ItemDao.all().single()
      assertEquals(0, item.slotIndex)
      assertEquals(HttpStatusCode.OK, response.status)
    }
  }

  @Test
  fun acceptNewItems_fullInventory() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val inventory = createTestInventory(user, app, 1)
    val itemClass = createTestItemClass(app)
    createTestItem(itemClass, inventory)
    createTestItem(itemClass, inventory)

    loginUser(user)

    val response = client.post("inventory/${inventory.id.value}/accept")

    transaction {
      val items = ItemDao.all()
      val unacceptedCount = items.count { it.slotIndex == null }
      assertEquals(2, items.count())
      assertEquals(1, unacceptedCount)
      assertEquals(HttpStatusCode.OK, response.status)
    }
  }

  @Test
  fun acceptNewItems_noInventory() = baseTestApp {
    val user = createTestUser()

    loginUser(user)

    val response = client.post("inventory/12345/accept")

    assertEquals(HttpStatusCode.BadRequest, response.status)
    assertErrorCode(ErrorCode.InventoryNotFound, response.body())
  }

  @Test
  fun swapItems_nonEmpty_success() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val inventory = createTestInventory(user, app)
    val itemClass = createTestItemClass(app)
    createTestItem(itemClass, inventory, 0)
    createTestItem(itemClass, inventory, 1)

    loginUser(user)

    val response = client.post("inventory/${inventory.id.value}/swap") {
      setBody(SwapItemsRequest(0, 1))
    }

    transaction {
      val items = ItemDao.all().toList()
      assertEquals(2, items.size)
      assertEquals(1, items[0].slotIndex)
      assertEquals(0, items[1].slotIndex)
      assertEquals(HttpStatusCode.OK, response.status)
    }
  }

  @Test
  fun swapItems_oneEmpty_success() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val inventory = createTestInventory(user, app)
    val itemClass = createTestItemClass(app)
    createTestItem(itemClass, inventory, 0)

    loginUser(user)

    val response = client.post("inventory/${inventory.id.value}/swap") {
      setBody(SwapItemsRequest(0, 1))
    }

    transaction {
      val item = ItemDao.all().single()
      assertEquals(1, item.slotIndex)
      assertEquals(HttpStatusCode.OK, response.status)
    }
  }

  @Test
  fun swapItems_bothEmpty_success() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val inventory = createTestInventory(user, app)
    val itemClass = createTestItemClass(app)
    val item = createTestItem(itemClass, inventory, 2)

    loginUser(user)

    val response = client.post("inventory/${inventory.id.value}/swap") {
      setBody(SwapItemsRequest(0, 1))
    }

    transaction {
      assertEquals(1, ItemDao.count())
      assertEquals(2, item.slotIndex)
      assertEquals(HttpStatusCode.OK, response.status)
    }
  }

  @Test
  fun swapItems_noInventory() = baseTestApp {
    val user = createTestUser()

    loginUser(user)

    val response = client.post("inventory/12345/swap") {
      setBody(SwapItemsRequest(0, 1))
    }

    assertEquals(HttpStatusCode.BadRequest, response.status)
    assertErrorCode(ErrorCode.InventoryNotFound, response.body())
  }
}