package com.javalinist

import com.javalinist.handlers.StatusHandler
import com.javalinist.handlers.UserHandler
import com.javalinist.handlers.UserSseHandler
import com.javalinist.logic.DB
import com.javalinist.logic.UserBroadcast
import com.javalinist.logic.users_table
import com.javalinist.web_handlers.IndexHandler
import com.javalinist.web_handlers.SseWebHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.logging.Logger

private fun getOpenApiOptions(): OpenApiOptions {
    val applicationInfo = Info().version("1.0").description("For testing Javalin")
    return OpenApiOptions(applicationInfo)
        .path("/swagger-docs")
        .ignorePath("/sse")
        .ignorePath("/sse/users")
        .ignorePath("/")
        .ignorePath("/status")
        .swagger(SwaggerOptions("/swagger"))
}

class JavalinistApplication {
    private val logger = Logger.getLogger(JavalinistApplication::class.java.name)

    val app: Javalin by lazy {
        val app = Javalin.create { config ->
            config.defaultContentType = "application/json"
            // verbose everywhere except prod
            if (System.getenv("env") == "prod") {
                config.requestLogger { ctx, executionTimeMs ->
                    logger.info("${ctx.method()} ${ctx.path()} -> ${ctx.res.status} [${ctx.res.contentType}] (took ${executionTimeMs} ms)")
                }
            } else {
                config.enableDevLogging()
            }
            config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
        }

        app
    }

    fun start(port: Int = 8080) {
        app.routes {
            ApiBuilder.crud("/users/:id", UserHandler())
            val sseHandler = UserSseHandler()
            ApiBuilder.sse("/sse/users", { client ->
                sseHandler.handle(client)
            })

            // status check
            ApiBuilder.get("/status", StatusHandler())

            // Web
            ApiBuilder.get("/", IndexHandler())
            ApiBuilder.get("/sse", SseWebHandler())
        }

        DB.run {
            transaction {
                SchemaUtils.create(users_table)
            }
        }
        app.start(port)

        UserBroadcast.getInstance().cleanUp()
    }

    fun stop() {
        app.stop()
    }
}

fun main() {
    JavalinistApplication().start()
}