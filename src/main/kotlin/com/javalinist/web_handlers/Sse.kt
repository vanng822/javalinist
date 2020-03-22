package com.javalinist.web_handlers

import io.javalin.http.Context
import io.javalin.http.Handler

class SseWebHandler: Handler {

    override fun handle(ctx: Context) {
        ctx.render("/templates/sse.html")
    }
}