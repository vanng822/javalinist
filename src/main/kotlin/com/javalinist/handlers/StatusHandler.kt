package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import io.javalin.http.Context
import io.javalin.http.Handler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class StatusHandler: Handler {
    val executors = Executors.newFixedThreadPool(2)

    override fun handle(ctx: Context) {
        // response with CompletableFuture
        ctx.json(fakeDoingSomethingUselessSlow())
    }

    private fun fakeDoingSomethingUselessSlow(): CompletableFuture<Any> {
        return CompletableFuture<Any>().apply {
            executors.submit {
                this.complete(object {
                    val status = ResponseStatus.OK
                })
            }
        }
    }
}