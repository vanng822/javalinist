package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import com.javalinist.logic.UserBroadcast
import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context

class UserHandler: BaseHandler, CrudHandler {
    private val users: UserBroadcast = UserBroadcast.getInstance()

    override fun getAll(ctx: Context) {
        val sortBy = ctx.queryParam("sortBy")
        val order = ctx.queryParam("order")
        response(ctx, 200, ResponseStatus.OK, users.sort(sortBy, order))
    }

    override fun getOne(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        val user = users.findUser(userId)
        if (user == null) {
            response(ctx, 404, ResponseStatus.NOT_FOUND)
            return
        }
        response(ctx, 200, ResponseStatus.OK, user)
    }

    override fun create(ctx: Context) {
        val name: String? = ctx.queryParam("name")
        if (name == null || name == "") {
            response(ctx, 400, ResponseStatus.INVALID)
            return
        }
        if (users.findUser(name) != null) {
            response(ctx, 409, ResponseStatus.INVALID)
            return
        }

        val user = users.createUser(name)

        response(ctx, 201, ResponseStatus.OK, user)
    }

    override fun update(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        val name: String? = ctx.queryParam("name")
        if (name == null || name == "") {
            response(ctx, 400, ResponseStatus.INVALID)
            return
        }
        var user = users.findUser(userId)
        if (user == null) {
            response(ctx, 404, ResponseStatus.NOT_FOUND, object {
                val id = userId
            })
            return
        }

        if (users.findUser(name) != null) {
            response(ctx, 409, ResponseStatus.INVALID)
            return
        }

        users.updateUser(user, name)
        response(ctx)
    }

    override fun delete(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        users.removeUser(userId)
        response(ctx)
    }
}