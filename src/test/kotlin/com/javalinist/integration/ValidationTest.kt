package com.javalinist.integration

import com.javalinist.JavalinistApplication
import com.javalinist.logic.DB
import com.javalinist.logic.users_table
import kong.unirest.Unirest
import org.assertj.core.api.Assertions
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.FixMethodOrder
import org.junit.jupiter.api.*
import org.junit.runners.MethodSorters

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Users API - Validation")
@FixMethodOrder(MethodSorters.JVM)
@Tag("validation")
class UsersValidationTest: BaseTest() {

    @Test
    fun `Get user with none Int for id should get 400`() {
        val response = Unirest.get("http://localhost:${port}/api/users/blabla")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Delete user with none Int for id should get 400`() {
        val response = Unirest.delete("http://localhost:${port}/api/users/blabla")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Create user with invalid chars for name should get 400`() {
        val response = Unirest.post("http://localhost:${port}/api/users")
            .body("{\"name\":\"åäö\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Create user with empty name should get 400`() {
        val response = Unirest.post("http://localhost:${port}/api/users")
            .body("{\"name\":\"\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }

    @Test
    fun `Create user with name longer than 99 should get 400`() {
        val name = "n".repeat(100)
        val response = Unirest.post("http://localhost:${port}/api/users")
            .body("{\"name\":\"${name}\"}")
        Assertions.assertThat(response.asEmpty().status).isEqualTo(400)
    }
}