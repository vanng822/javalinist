package com.javalinist.handlers

import com.javalinist.logic.UserBroadcast
import io.javalin.http.sse.SseClient
import io.javalin.plugin.json.JavalinJackson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.consumeEach
import java.util.function.Consumer

class UserSseHandler {
    private val users: UserBroadcast = UserBroadcast.getInstance()

    fun handle(client: SseClient) {
        GlobalScope.async {
            users.userEvents.consumeEach { userEvent ->
                client.sendEvent(userEvent.type.toString(), JavalinJackson.toJson(userEvent.user))
            }
        }
    }
}