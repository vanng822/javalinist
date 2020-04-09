package com.javalinist

import com.javalinist.handlers.StatusHandler
import com.javalinist.handlers.UserHandler
import com.javalinist.handlers.UserSseHandler
import com.javalinist.logic.DB
import com.javalinist.logic.DB_OPTIONS
import com.javalinist.logic.UserBroadcast
import com.javalinist.logic.users_table
import com.javalinist.validators.registerValidators
import com.javalinist.web_handlers.IndexHandler
import com.javalinist.web_handlers.SseWebHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.staticfiles.Location
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.timerTask

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
            config.addStaticFiles("src/main/resources/static", Location.EXTERNAL)
        }

        app
    }

    fun start(port: Int = 8080) {
        app.routes {
            ApiBuilder.path("api") {
                ApiBuilder.crud("/users/:id", UserHandler())
            }
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
        registerValidators()
        DB.run {
            transaction {
                SchemaUtils.create(users_table)
            }
        }
        app.start(port)

        UserBroadcast.getInstance().cleanUp()
        printStats()
    }

    fun stop() {
        app.stop()
    }

    private fun printStats() {
        GlobalScope.launch {
            val timer = Timer()
            timer.schedule(
                timerTask {
                    logger.info("Number of running threads: ${Thread.activeCount()}")
                },
                10000, 10000
            )
        }
    }
}

fun main() {
    JavalinistApplication().start()
}