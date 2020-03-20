package com.javalinist

import com.javalinist.handlers.UserHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder

fun main() {
    Javalin.create { config ->
        config.defaultContentType = "application/json"
        config.enableDevLogging()
    }.routes {
        ApiBuilder.crud("/users/:id", UserHandler())
    }.start(8080)
}