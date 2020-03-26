package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.Handler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StatusHandler: Handler {
    override fun handle(ctx: Context) {
        // response with CompletableFuture
        ctx.json(fakeDoingSomethingUselessSlow())
    }

    private fun fakeDoingSomethingUselessSlow(): CompletableFuture<Any> {
        return CompletableFuture<Any>().apply {
            Executors.newSingleThreadScheduledExecutor().schedule({
                this.complete(object {
                    val status = ResponseStatus.OK
                })},
                0,
                TimeUnit.SECONDS)
        }
    }
}