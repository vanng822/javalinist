package com.javalinist.logic

import com.javalinist.enums.UserEventType
import com.javalinist.models.User
import com.javalinist.models.UserEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BroadcastChannel
import java.util.logging.Logger

class UserBroadcast private constructor(): Users() {
    val userEvents = BroadcastChannel<UserEvent>(10)

    private val logger = Logger.getLogger(UserBroadcast::class.java.name)

    override fun createUser(name: String): User {
        val user = super.createUser(name)
        GlobalScope.async {
            logger.info("${user}")
            userEvents.send(
                UserEvent(
                    UserEventType.CREATED,
                    user
                )
            )
        }
        return user
    }

    override fun updateUser(user: User, name: String): User {
        val updatedUser = super.updateUser(user, name)

        GlobalScope.async {
            logger.info("${updatedUser}")
            userEvents.send(
                UserEvent(
                    UserEventType.UPDATED,
                    updatedUser
                )
            )
        }

        return updatedUser
    }

    override fun removeUser(userId: Int) {
        super.removeUser(userId)

        GlobalScope.async {
            // just id, no name when deleted
            val user = User(userId, "")
            logger.info("${user}")
            userEvents.send(
                UserEvent(
                    UserEventType.DELETED,
                    user
                )
            )
        }
    }

    companion object {
        private var instance: UserBroadcast = UserBroadcast()

        fun getInstance(): UserBroadcast {
            return instance
        }
    }
}