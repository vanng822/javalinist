package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import com.javalinist.models.User
import io.javalin.http.Context
import javax.lang.model.type.NullType

interface BaseHandler {
    fun response(cxt: Context, statusCode: Int = 200, response: Response<*> = NullResponse(status = ResponseStatus.OK)) {
        cxt.status(statusCode)
        cxt.json(response)
    }
}

class Response<T>(status: ResponseStatus, result: T? = null) {
    val status = status
    val result: T? = result
}

typealias UserResponse = Response<User>
typealias UsersResponse = Response<List<User>>
typealias NullResponse = Response<NullType>
typealias AnyResponse = Response<Any>