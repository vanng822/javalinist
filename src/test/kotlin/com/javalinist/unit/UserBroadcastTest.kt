package com.javalinist.unit

import com.javalinist.enums.UserEventType
import com.javalinist.logic.UserBroadcast
import com.javalinist.models.User
import com.javalinist.models.UserEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.consumeEach
import org.junit.jupiter.api.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("UserBroadcast")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class UserBroadcastTest: BaseTest {
    lateinit var users: UserBroadcast

    @BeforeAll
    fun setUp() {
        users = UserBroadcast.getInstance()
    }

    @AfterAll
    fun tearDown() {
    }


    @Test
    fun userEvents() {
        val actual: MutableList<UserEvent> = mutableListOf()

        GlobalScope.async {
            users.userEvents.consumeEach {
                actual.add(it)
            }
        }
        val user = users.createUser("Nguyen")
        // Wait for broadcasting
        Thread.sleep(50)
        val updatedUser = users.updateUser(user, "Van")
        Thread.sleep(50)
        users.removeUser(user.id)
        Thread.sleep(50)

        val expected: MutableList<UserEvent> = mutableListOf()
        expected.add(UserEvent(UserEventType.CREATED, user))
        expected.add(UserEvent(UserEventType.UPDATED, updatedUser))
        expected.add(UserEvent(UserEventType.DELETED, User(user.id, "")))
        Assertions.assertEquals(expected, actual)
    }
}