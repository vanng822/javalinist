package com.javalinist

import com.javalinist.web_handlers.IndexHandler
import com.javalinist.handlers.UserHandler
import com.javalinist.handlers.UserSseHandler
import com.javalinist.logic.getDb
import com.javalinist.logic.users_table
import com.javalinist.web_handlers.SseWebHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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

        // Web
        ApiBuilder.get("/", IndexHandler())
        ApiBuilder.get("/sse", SseWebHandler())
    }

    val db = getDb()
    db.run {
        transaction {
            SchemaUtils.create(users_table)
        }
    }
    app.start(8080)
}