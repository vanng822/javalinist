package com.javalinist

import com.javalinist.models.User
import kong.unirest.Unirest
import org.assertj.core.api.Assertions
import org.junit.FixMethodOrder
import org.junit.jupiter.api.*
import org.junit.runners.MethodSorters
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Users API - Validation")
@FixMethodOrder(MethodSorters.JVM)
class UsersValidationTest {

    private lateinit var app: JavalinistApplication
    private var port = 5003

    @BeforeAll
    fun setUp() {
        app = JavalinistApplication()
        app.start(port)
    }

    @AfterAll
    fun tearDown() {
        app.stop()
    }

    @Test
    fun `Get user with none Int for id should get 400`() {
        val response = Unirest.get("http://localhost:${port}/users/blabla")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Delete user with none Int for id should get 400`() {
        val response = Unirest.delete("http://localhost:${port}/users/blabla")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Create user with invalid chars for name should get 400`() {
        val response = Unirest.post("http://localhost:${port}/users")
            .body("{\"name\":\"åäö\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Create user with empty name should get 400`() {
        val response = Unirest.post("http://localhost:${port}/users")
            .body("{\"name\":\"\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Create user with name longer than 99 should get 400`() {
        val name = "n".repeat(100)
        val response = Unirest.post("http://localhost:${port}/users")
            .body("{\"name\":\"${name}\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }
}