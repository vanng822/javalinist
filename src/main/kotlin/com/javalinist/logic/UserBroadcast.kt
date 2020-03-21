package com.javalinist.logic

import com.javalinist.enums.UserEventType
import com.javalinist.models.User
import com.javalinist.models.UserEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BroadcastChannel
import java.util.logging.Logger

class UserBroadcast: Users() {
    val userEvents = BroadcastChannel<UserEvent>(1)
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

    override fun updateUser(user: User, name: String) {
        super.updateUser(user, name)
        GlobalScope.async {
            logger.info("${user}")
            userEvents.send(
                UserEvent(
                    UserEventType.UPDATED,
                    user
                )
            )
        }
    }

    override fun removeUser(userId: Int) {
        super.removeUser(userId)
        // just id, no name when deleted
        val user = User(userId, "")
        GlobalScope.async {
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
        private var instance: UserBroadcast? = null

        fun getInstance(): UserBroadcast {
            if (instance != null) {
                return instance as UserBroadcast
            }
            return synchronized(this) {
                val i2 = instance
                if (i2 != null) {
                    return i2
                }
                instance = UserBroadcast()
                return instance as UserBroadcast
            }
        }
    }
}