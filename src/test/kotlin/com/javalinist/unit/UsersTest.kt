package com.javalinist.unit

import com.javalinist.enums.OrderOptions
import com.javalinist.enums.SortByOptions
import com.javalinist.logic.DbUser
import com.javalinist.logic.Users
import com.javalinist.models.User
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.lang.Exception


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Users")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class UsersTests: BaseTest {
    lateinit var users: Users
    @BeforeAll
    fun setUp() {
        users = Users()
    }

    @AfterAll
    fun tearDown() {
    }

    @Test
    fun create() {
        val expected = User(1, "Nguyen")
        val actual = users.createUser("Nguyen")
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun update() {
        val user = users.createUser("Nguyen")
        val expected = User(user.id, "Van")
        val actual = users.updateUser(user, "Van")
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun findUserByName() {
        val expected = users.createUser("Nguyen")
        val actual = users.findUser(expected.name)
        Assertions.assertEquals(expected, actual)

        // Expected throw
        // But why this exception???
        assertThrows<NoSuchElementException> {
            users.findUser("Van")
        }
    }

    @Test
    fun findUserById() {
        val expected = users.createUser("Nguyen")
        val actual = users.findUser(expected.id)
        Assertions.assertEquals(expected, actual)

        // Expected throw
        assertThrows<EntityNotFoundException> {
            users.findUser(999999999)
        }
    }

    @Test
    fun remove() {
        val user = users.createUser("Nguyen")
        users.removeUser(user.id)

        // Remove on non-existens not throw
        Assertions.assertDoesNotThrow({
                users.removeUser(999999999)
            }
        )
    }

    @Test
    fun sort() {
        val nUser = users.createUser("Nguyen")
        val vUser = users.createUser("Van")

        val actualIdDesc = users.sort(SortByOptions.ID, OrderOptions.DESC)
        val expectedIdDesc = listOf(vUser, nUser)
        Assertions.assertEquals(actualIdDesc, expectedIdDesc)

        val actualNameDesc = users.sort(SortByOptions.NAME, OrderOptions.DESC)
        val expectedNameDesc = listOf(vUser, nUser)
        Assertions.assertEquals(actualNameDesc, expectedNameDesc)


        val actualNameAsc = users.sort(SortByOptions.NAME, OrderOptions.ASC)
        val expectedNameAsc = listOf(nUser, vUser)
        Assertions.assertEquals(actualNameAsc, expectedNameAsc)

        val actualIdAsc = users.sort(SortByOptions.ID, OrderOptions.ASC)
        val expectedIdAsc = listOf(nUser, vUser)
        Assertions.assertEquals(actualIdAsc, expectedIdAsc)
    }
}