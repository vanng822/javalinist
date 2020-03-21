package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import io.javalin.http.Context
import io.javalin.http.Handler

class IndexHandler: BaseHandler, Handler {

    override fun handle(ctx: Context) {
        response(ctx, 200,ResponseStatus.OK, object {
            val endpoints = listOf<Any>(
                object {
                    val endpoint = "/users"
                    val type = "CRUD"
                    val options = "GET, POST, PATCH, DELETE"
                },
                object {
                    val endpoint = "/sse/users"
                    val type = "SSE"
                }
            )
        })
    }
}