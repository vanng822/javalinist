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
internal class UsersCleanupTest: BaseTest {
    lateinit var users: Users
    @BeforeAll
    fun setUp() {
        users = Users()
        users.maxSize = 1
        users.cleanUpDelay = 0
        users.cleanUpPeriod = 300
    }

    @AfterAll
    fun tearDown() {
    }


    @Test
    fun cleanUp() {
        users.createUser("Nguyen")
        users.createUser("Van")
        var c:Int = 0
        transaction {
            c = DbUser.count().toInt()
        }
        Assertions.assertEquals(2, c)
        users.cleanUp()
        Thread.sleep(400)
        transaction {
            c = DbUser.count().toInt()
        }
        Assertions.assertEquals(1, c)
    }
}