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
        ctx.json(users)
    }

    override fun getOne(ctx: Context, resourceId: String) {
        val userId: Int = resourceId.toInt()
        val user = users.findUser(userId)
        if (user == null) {
            ctx.status(404).json(object {
                val status = "NotFound"
            })
            return
        }
        ctx.json(user)
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

        var res = object {
            val status = "OK"
            val id = user.id
        }
        ctx.status(201).json(res)
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