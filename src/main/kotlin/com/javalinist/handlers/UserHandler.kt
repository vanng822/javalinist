package com.javalinist.handlers

import com.javalinist.logic.*
import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context

class UserHandler: BaseHandler, CrudHandler {
    private val users: Users = mutableListOf()

    override fun getAll(ctx: Context) {
        val order = ctx.queryParam("order", "ASC")
        if (order == "DESC") {
            response(ctx, 200, ResponseStatus.OK, users.sortByDescending { it.name })
        }
        response(ctx, 200, ResponseStatus.OK, users)
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

        val id: Int = users.createUser(name)

        response(ctx, 201, ResponseStatus.OK, object {
            val id = id
        })
    }

    override fun update(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        val name: String? = ctx.queryParam("name")
        if (name == null || name == "") {
            response(ctx, 400,ResponseStatus.INVALID)
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

        user.name = name
        response(ctx)
    }

    override fun delete(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        users.removeUser(userId)
        response(ctx)
    }
}