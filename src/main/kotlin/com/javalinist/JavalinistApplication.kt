package com.javalinist

import com.javalinist.handlers.UserHandler
import com.javalinist.handlers.UserSseHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder

fun main() {
    val app = Javalin.create { config ->
        config.defaultContentType = "application/json"
        config.enableDevLogging()
    }.routes {
        ApiBuilder.crud("/users/:id", UserHandler())
        val sseHandler = UserSseHandler()
        ApiBuilder.sse("/sse/users", {
            client -> sseHandler.handle(client)
        })
    }
    app.start(8080)
}