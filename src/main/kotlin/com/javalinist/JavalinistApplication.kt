package com.javalinist

import com.javalinist.controllers.UserHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder

fun main() {
    val app = Javalin.create{ config ->
        config.defaultContentType = "application/json"
    }.start(8080)

    app.routes {
        ApiBuilder.crud("/users/:id", UserHandler())
    }
}