package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import io.javalin.http.Context
import io.javalin.http.Handler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class StatusHandler: Handler {
    val executors = ThreadPoolExecutor(
        1,
        2,
        60L,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue<Runnable>()
    )

    override fun handle(ctx: Context) {
        // response with CompletableFuture
        ctx.json(fakeDoingSomethingUselessSlow())
    }

    private fun fakeDoingSomethingUselessSlow(): CompletableFuture<Any> {
        return CompletableFuture<Any>().apply {
            executors.submit {
                this.complete(NullResponse(status = ResponseStatus.OK))
            }
        }
    }
}