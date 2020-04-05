package com.javalinist.integration

import com.javalinist.JavalinistApplication
import com.javalinist.logic.DB
import com.javalinist.logic.users_table
import com.javalinist.models.User
import kong.unirest.Unirest
import org.assertj.core.api.Assertions
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*

data class TestUsersResponse(val status: String, val result: List<User>)
data class TestUserResponse(val status: String, val result: User)


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Users API - Flow")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UsersTest {

    private lateinit var app: JavalinistApplication
    private var port = 5001

    @BeforeAll
    fun setUp() {
        app = JavalinistApplication()
        app.start(port)
    }

    @AfterAll
    fun tearDown() {
        app.stop()
        DB.run {
            transaction {
                SchemaUtils.drop(users_table)
            }
        }
    }

    @Test
    @Order(1)
    @Tag("flows")
    fun `Create should give 201`() {
        val response = Unirest.post("http://localhost:${port}/api/users")
            .body("{\"name\":\"Nguyen\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(201)
    }

    @Test
    @Order(2)
    @Tag("flows")
    fun `Get user should give newly created user`() {
        val response = Unirest.get("http://localhost:${port}/api/users/1")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val user: TestUserResponse = response.asObject(TestUserResponse::class.java).getBody()
        Assertions.assertThat(user.status).isEqualTo("OK")
        val expected: User = User(1, "Nguyen")
        Assertions.assertThat(user.result).isEqualTo(expected)
    }

    @Test
    @Order(3)
    @Tag("flows")
    fun `Patch user with new name should give 200`() {
        val response = Unirest.patch("http://localhost:${port}/api/users/1")
            .body("{\"name\":\"Van\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
    }

    @Test
    @Order(4)
    @Tag("flows")
    fun `Get users should content 1 user with new name`() {
        val response = Unirest.get("http://localhost:${port}/api/users")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val users: TestUsersResponse = response.asObject(
            TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        val expected: List<User> = listOf(User(1, "Van"))
        Assertions.assertThat(users.result).isEqualTo(expected)
    }

    @Test
    @Order(5)
    @Tag("flows")
    fun `Delete from user should get OK`() {
        val response = Unirest.delete("http://localhost:${port}/api/users/1")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val users: TestUsersResponse = response.asObject(
            TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        Assertions.assertThat(users.result).isNull()
    }

    @Test
    @Order(6)
    @Tag("flows")
    fun `List after deletion should be empty list`() {
        val response = Unirest.get("http://localhost:${port}/api/users")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val users: TestUsersResponse = response.asObject(
            TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        Assertions.assertThat(users.result).isEmpty()
    }

    @Test
    @Order(7)
    @Tag("flows")
    fun `Get after deletion should get 404`() {
        val response = Unirest.get("http://localhost:${port}/api/users/1")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(404)
    }

    @Test
    @Order(8)
    fun `Sort and sort order`() {
        val resp1 = Unirest.post("http://localhost:${port}/api/users")
            .body("{\"name\":\"Nguyen\"}")
        Assertions.assertThat(resp1.asEmpty().status).isEqualTo(201)
        val resp2 = Unirest.post("http://localhost:${port}/api/users")
            .body("{\"name\":\"Van\"}")
        Assertions.assertThat(resp2.asEmpty().status).isEqualTo(201)

        var response2 = Unirest.get("http://localhost:${port}/api/users?sortBy=name&order=desc")
        Assertions.assertThat(response2.asEmpty().status).isEqualTo(200)
        var users: TestUsersResponse = response2.asObject(
            TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        var expected: List<User> = listOf(User(3, "Van"), User(2, "Nguyen"))
        Assertions.assertThat(users.result).isEqualTo(expected)

        response2 = Unirest.get("http://localhost:${port}/api/users?sortBy=id&order=desc")
        Assertions.assertThat(response2.asEmpty().status).isEqualTo(200)
        users = response2.asObject(TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        expected = listOf(User(3, "Van"), User(2, "Nguyen"))
        Assertions.assertThat(users.result).isEqualTo(expected)


        response2 = Unirest.get("http://localhost:${port}/api/users?sortBy=name&order=asc")
        Assertions.assertThat(response2.asEmpty().status).isEqualTo(200)
        users = response2.asObject(TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        expected = listOf(User(2, "Nguyen"), User(3, "Van"))
        Assertions.assertThat(users.result).isEqualTo(expected)

        response2 = Unirest.get("http://localhost:${port}/api/users?sortBy=id&order=asc")
        Assertions.assertThat(response2.asEmpty().status).isEqualTo(200)
        users = response2.asObject(TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        expected = listOf(User(2, "Nguyen"), User(3, "Van"))
        Assertions.assertThat(users.result).isEqualTo(expected)
    }
}