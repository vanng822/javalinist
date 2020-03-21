package com.javalinist.handlers

import com.javalinist.logic.UserBroadcast
import io.javalin.http.sse.SseClient
import io.javalin.plugin.json.JavalinJackson
import kotlinx.coroutines.channels.consumeEach

class UserSseHandler {
    private val users: UserBroadcast = UserBroadcast.getInstance()

    suspend fun handle(client: SseClient) {
        users.userEvents.consumeEach { userEvent ->
            client.sendEvent(userEvent.eventType.toString(), JavalinJackson.toJson(userEvent.user))
        }
    }
}