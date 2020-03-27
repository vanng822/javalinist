package com.javalinist

import com.javalinist.models.User
import kong.unirest.Unirest
import org.assertj.core.api.Assertions
import org.junit.FixMethodOrder
import org.junit.jupiter.api.*
import org.junit.runners.MethodSorters

data class TestUsersResponse(val status: String, val result: List<User>)
data class TestUserResponse(val status: String, val result: User)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Users API")
@FixMethodOrder(MethodSorters.JVM)
class UsersTest {

    private lateinit var app: JavalinistApplication

    @BeforeAll
    fun setUp() {
        app = JavalinistApplication()
        app.start(8080)
    }

    @AfterAll
    fun tearDown() {
        app.stop()
    }

    @Test
    fun `BetterWayButNotNow1`() {
        val response = Unirest.post("http://localhost:8080/users?name=Nguyen")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(201)
    }

    @Test
    fun `BetterWayButNotNow2`() {
        val response = Unirest.get("http://localhost:8080/users/1")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val user: TestUserResponse = response.asObject(TestUserResponse::class.java).getBody()
        Assertions.assertThat(user.status).isEqualTo("OK")
        val expected: User = User(1, "Nguyen")
        Assertions.assertThat(user.result).isEqualTo(expected)
    }

    @Test
    fun `BetterWayButNotNow3`() {
        val response = Unirest.patch("http://localhost:8080/users/1?name=Van")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
    }

    @Test
    fun `BetterWayButNotNow4`() {
        val response = Unirest.get("http://localhost:8080/users")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val users: TestUsersResponse = response.asObject(TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        val expected: List<User> = listOf(User(1, "Van"))
        Assertions.assertThat(users.result).isEqualTo(expected)
    }

    @Test
    fun `BetterWayButNotNow5`() {
        val response = Unirest.delete("http://localhost:8080/users/1")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val users: TestUsersResponse = response.asObject(TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        Assertions.assertThat(users.result).isNull()
    }

    @Test
    fun `BetterWayButNotNow6`() {
        val response = Unirest.get("http://localhost:8080/users")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(200)
        val users: TestUsersResponse = response.asObject(TestUsersResponse::class.java).getBody()
        Assertions.assertThat(users.status).isEqualTo("OK")
        Assertions.assertThat(users.result).isEmpty()
    }
}