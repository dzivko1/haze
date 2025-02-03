package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.item.model.CreateItemsRequest
import io.github.dzivko1.haze.data.item.model.CreateItemsResponse
import io.github.dzivko1.haze.server.TestBase
import io.github.dzivko1.haze.server.data.hazeApp.model.HazeAppsTable
import io.github.dzivko1.haze.server.data.item.model.InventoriesTable
import io.github.dzivko1.haze.server.data.item.model.ItemClassesTable
import io.github.dzivko1.haze.server.data.item.model.ItemDao
import io.github.dzivko1.haze.server.data.item.model.ItemsTable
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import io.github.dzivko1.haze.server.util.createTestApp
import io.github.dzivko1.haze.server.util.createTestInventory
import io.github.dzivko1.haze.server.util.createTestItemClass
import io.github.dzivko1.haze.server.util.createTestUser
import io.github.dzivko1.haze.server.util.loginUser
import io.github.dzivko1.haze.server.util.suspendTransaction
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.andWhere
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.toKotlinUuid

class ItemRoutesTest : TestBase(InventoriesTable, ItemClassesTable, ItemsTable, HazeAppsTable, UsersTable) {

  @Test
  fun createItems_directDesignation_success() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val inventory = createTestInventory(user, app)
    val itemClass = createTestItemClass(app)

    loginUser(user)

    val response = client.post("items") {
      setBody(
        CreateItemsRequest(
          listOf(
            CreateItemsRequest.DirectItemDesignation(itemClass.id.value, inventory.id.value),
            CreateItemsRequest.DirectItemDesignation(itemClass.id.value, inventory.id.value)
          )
        )
      )
    }

    suspendTransaction {
      val items = ItemDao.all()
      assertEquals(2, items.count())
      items.forEach { item ->
        assertEquals(itemClass.id.value, item.itemClass.id.value)
        assertEquals(inventory.id.value, item.inventory.id.value)
        assertEquals(item.id.value, item.originalId)
        assertNull(item.nextId)
        assertNull(item.slotIndex)
        assertTrue(item.isNew)
      }
      assertEquals(HttpStatusCode.OK, response.status)
      assertEquals(2, response.body<CreateItemsResponse>().itemIds.size)
    }
  }

  @Test
  fun createItems_indirectDesignation_success() = baseTestApp {
    val user = createTestUser()
    val app = createTestApp()
    val itemClass = createTestItemClass(app)

    loginUser(user)

    suspendTransaction {
      val response = client.post("items") {
        setBody(
          CreateItemsRequest(
            listOf(
              CreateItemsRequest.IndirectItemDesignation(
                itemClass.id.value,
                user.id.value.toKotlinUuid(),
                app.id.value
              ),
              CreateItemsRequest.IndirectItemDesignation(
                itemClass.id.value,
                user.id.value.toKotlinUuid(),
                app.id.value
              )
            )
          )
        )
      }

      val items = ItemDao.all()
      assertEquals(2, items.count())
      items.forEach { item ->
        val inventoryId = InventoriesTable.select(InventoriesTable.id)
          .where { InventoriesTable.user eq user.id }
          .andWhere { InventoriesTable.app eq app.id }
          .single()[InventoriesTable.id].value
        assertEquals(itemClass.id.value, item.itemClass.id.value)
        assertEquals(inventoryId, item.inventory.id.value)
        assertEquals(item.id.value, item.originalId)
        assertNull(item.nextId)
        assertNull(item.slotIndex)
        assertTrue(item.isNew)
      }
      assertEquals(HttpStatusCode.OK, response.status)
      assertEquals(2, response.body<CreateItemsResponse>().itemIds.size)
    }
  }

  @Test
  fun createItems_mixedDesignation_success() = baseTestApp {
    val user1 = createTestUser("user1")
    val user2 = createTestUser("user2")
    val app1 = createTestApp("app1")
    val app2 = createTestApp("app2")
    val inventory1 = createTestInventory(user1, app1)
    val inventory2 = createTestInventory(user2, app1)
    val inventory3 = createTestInventory(user2, app2)
    val itemClass1 = createTestItemClass(app1, "itemClass1")
    val itemClass2 = createTestItemClass(app2, "itemClass2")

    loginUser(user1)

    suspendTransaction {
      val response = client.post("items") {
        setBody(
          CreateItemsRequest(
            listOf(
              CreateItemsRequest.DirectItemDesignation(itemClass1.id.value, inventory1.id.value),
              CreateItemsRequest.IndirectItemDesignation(
                itemClass1.id.value,
                inventory1.user.id.value.toKotlinUuid(),
                inventory1.app.id.value
              ),
              CreateItemsRequest.DirectItemDesignation(itemClass1.id.value, inventory2.id.value),
              CreateItemsRequest.DirectItemDesignation(itemClass2.id.value, inventory3.id.value)
            )
          )
        )
      }

      val items = ItemDao.all()
      assertEquals(4, items.count())
      items.forEachIndexed { index, item ->
        assertEquals(item.id.value, item.originalId)
        assertNull(item.nextId)
        assertNull(item.slotIndex)
        assertTrue(item.isNew)

        when (index) {
          0 -> {
            assertEquals(itemClass1.id.value, item.itemClass.id.value)
            assertEquals(inventory1.id.value, item.inventory.id.value)
          }

          1 -> {
            assertEquals(itemClass1.id.value, item.itemClass.id.value)
            assertEquals(inventory1.id.value, item.inventory.id.value)
          }

          2 -> {
            assertEquals(itemClass1.id.value, item.itemClass.id.value)
            assertEquals(inventory2.id.value, item.inventory.id.value)
          }

          3 -> {
            assertEquals(itemClass2.id.value, item.itemClass.id.value)
            assertEquals(inventory3.id.value, item.inventory.id.value)
          }
        }
      }
      assertEquals(HttpStatusCode.OK, response.status)
      assertEquals(4, response.body<CreateItemsResponse>().itemIds.size)
    }
  }

  @Test
  fun createItems_itemIncompatibleWithApp() = baseTestApp {
    val user = createTestUser()
    val app1 = createTestApp("The app")
    val app2 = createTestApp("Some app")
    val inventory1 = createTestInventory(user, app1)
    val inventory2 = createTestInventory(user, app2)
    val itemClass = createTestItemClass(app1)

    loginUser(user)

    suspendTransaction {
      val response = client.post("items") {
        setBody(
          CreateItemsRequest(
            listOf(
              CreateItemsRequest.DirectItemDesignation(itemClass.id.value, inventory2.id.value),
              CreateItemsRequest.IndirectItemDesignation(
                itemClass.id.value,
                inventory2.user.id.value.toKotlinUuid(),
                inventory2.app.id.value
              )
            )
          )
        )
      }

      assertEquals(0, ItemDao.count())
      assertEquals(HttpStatusCode.OK, response.status)
      assertEquals(0, response.body<CreateItemsResponse>().itemIds.size)
    }
  }
}