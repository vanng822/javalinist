package com.javalinist

import com.javalinist.handlers.UserHandler
import com.javalinist.handlers.UserSseHandler
import com.javalinist.logic.UserBroadcast
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.plugin.json.JavalinJackson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.consumeEach

fun main() {
    val app = Javalin.create { config ->
        config.defaultContentType = "application/json"
        config.enableDevLogging()
    }.routes {
        ApiBuilder.crud("/users/:id", UserHandler())
    }
    val sseHandler = UserSseHandler()

    app.sse("/sse/users") { client ->
        GlobalScope.async {
            sseHandler.handle(client)
        }
    }
    app.start(8080)
}