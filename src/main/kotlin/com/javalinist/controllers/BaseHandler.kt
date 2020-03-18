package com.javalinist.controllers

import io.javalin.http.Context

interface BaseHandler {
    fun response(cxt: Context, status: Int? = 200, result: Any? = object {
        val status = "OK"
    }) {
        if (status != null) {
            cxt.status(status)
        }
        if (result != null) {
            cxt.json(object {
                val result = result
            })
        }
    }
}
