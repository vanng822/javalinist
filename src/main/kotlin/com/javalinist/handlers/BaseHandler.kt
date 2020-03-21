package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import io.javalin.http.Context

interface BaseHandler {
    fun response(cxt: Context, statusCode: Int = 200, status: ResponseStatus = ResponseStatus.OK, result: Any? = null) {
        cxt.status(statusCode)

        cxt.json(object {
            val status = status
            val result = result
        })
    }
}
