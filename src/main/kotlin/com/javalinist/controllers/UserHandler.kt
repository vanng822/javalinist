package com.javalinist.controllers

import com.javalinist.logic.Users
import com.javalinist.logic.findUser
import com.javalinist.logic.nextId
import com.javalinist.logic.removeUser
import com.javalinist.models.User
import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context

class UserHandler: BaseHandler, CrudHandler {
    private val users: Users = mutableListOf()

    override fun getAll(ctx: Context) {
        response(ctx, 200, users)
    }

    override fun getOne(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        val user = users.findUser(userId)
        if (user == null) {
            response(ctx, 404, object {
                val status = "NotFound"
            })
            return
        }
        response(ctx, 200, user)
    }

    override fun create(ctx: Context) {
        val name: String? = ctx.queryParam("name")
        if (name == null || name == "") {
            response(ctx, 400, object {
                val status = "INVALID"
            })
            return
        }
        if (users.findUser(name) != null) {
            response(ctx, 409, object {
                val status = "INVALID"
            })
            return
        }
        val id: Int = users.nextId()
        val user = User(id, name)
        users.add(user)

        response(ctx, 201, object {
            val status = "OK"
            val id = user.id
        })
    }

    override fun update(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        val name: String? = ctx.queryParam("name")
        if (name == null || name == "") {
            response(ctx, 400, object {
                val status = "INVALID"
            })
            return
        }
        var user = users.findUser(userId)
        if (user == null) {
            response(ctx, 404, object {
                val status = "NotFound"
                val userId = userId
            })
            return
        }

        user.name = name
        response(ctx, 200, object {
            val status = "OK"
        })
    }

    override fun delete(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        users.removeUser(userId)
        response(ctx)
    }
}