package io.github.dzivko1.haze.server.routing

import io.github.dzivko1.haze.data.user.model.LoginResponse
import io.github.dzivko1.haze.data.user.model.UserAuthRequest
import io.github.dzivko1.haze.server.TestBase
import io.github.dzivko1.haze.server.data.user.model.UsersTable
import io.github.dzivko1.haze.server.exceptions.ErrorCode
import io.github.dzivko1.haze.server.util.assertErrorCode
import io.github.dzivko1.haze.server.util.createTestUser
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthRoutesTest : TestBase(UsersTable) {
  @Test
  fun register_success() = baseTestApp {
    val response = client.post("auth/register") {
      setBody(UserAuthRequest("uname", "pass"))
    }

    transaction {
      val user = UsersTable.select(UsersTable.name)
        .where { UsersTable.name eq "uname" }
        .singleOrNull()
      assertNotNull(user)
      assertEquals(HttpStatusCode.Created, response.status)
    }
  }

  @Test
  fun register_usernameTaken() = baseTestApp {
    client.post("auth/register") {
      setBody(UserAuthRequest("uname", "pass1"))
    }

    val response = client.post("auth/register") {
      setBody(UserAuthRequest("uname", "pass2"))
    }

    assertEquals(HttpStatusCode.BadRequest, response.status)
    assertErrorCode(ErrorCode.UsernameTaken, response.body())
  }

  @Test
  fun login_success() = baseTestApp {
    createTestUser()

    val response = client.post("auth/login") {
      setBody(UserAuthRequest("uname", "pass"))
    }

    assertEquals(HttpStatusCode.OK, response.status)
    val token = response.body<LoginResponse>().token
    assertTrue(token.isNotBlank())
  }

  @Test
  fun login_wrongPassword() = baseTestApp {
    createTestUser()

    val response = client.post("auth/login") {
      setBody(UserAuthRequest("uname", "wrongpass"))
    }

    assertEquals(HttpStatusCode.Unauthorized, response.status)
  }

  @Test
  fun login_nonExistingUser() = baseTestApp {
    val response = client.post("auth/login") {
      setBody(UserAuthRequest("uname", "pass"))
    }

    assertEquals(HttpStatusCode.Unauthorized, response.status)
  }
}