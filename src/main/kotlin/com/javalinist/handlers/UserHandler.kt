package com.javalinist.handlers

import com.javalinist.enums.ResponseStatus
import com.javalinist.logic.UserBroadcast
import com.javalinist.models.User
import io.javalin.apibuilder.CrudHandler
import io.javalin.core.validation.Validator
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiParam
import org.h2.jdbc.JdbcBatchUpdateException
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException


class UserHandler : BaseHandler, CrudHandler {
    private val users: UserBroadcast = UserBroadcast.getInstance()

    override fun getAll(ctx: Context) {
        val sortBy = ctx.queryParam("sortBy")
        val order = ctx.queryParam("order")
        response(ctx, 200, UsersResponse(ResponseStatus.OK, users.sort(sortBy, order)))
    }

    override fun getOne(ctx: Context, resourceId: String) {
        val userId: Int? = resourceId.toIntOrNull()
        if (userId == null) {
            response(ctx, 400, NullResponse(ResponseStatus.INVALID))
            return
        }
        val user: User
        try {
            user = users.findUser(userId)
        } catch (exc: EntityNotFoundException) {
            response(ctx, 404, NullResponse(ResponseStatus.NOT_FOUND))
            return
        }
        response(ctx, 200, UserResponse(ResponseStatus.OK, user))
    }

    @OpenApi(
        queryParams = [OpenApiParam(
            name = "name",
            required = true,
            description = "Allowed chars: a-zA-Z0-9 and whitespace"
        )]
    )
    override fun create(ctx: Context) {
        val name = validateName(ctx)
        if (name == null || name == "") {
            response(ctx, 400, NullResponse(ResponseStatus.INVALID))
            return
        }

        var user: User
        try {
            user = users.createUser(name)
        } catch (exc: ExposedSQLException) {
            if (exc.cause is JdbcBatchUpdateException || exc.cause is JdbcSQLIntegrityConstraintViolationException) {
                response(ctx, 409, NullResponse(ResponseStatus.INVALID))
                return
            }
            throw exc
        }

        response(ctx, 201, UserResponse(ResponseStatus.OK, user))
    }

    @OpenApi(
        queryParams = [OpenApiParam(
            name = "name",
            required = true,
            description = "Allowed chars: a-zA-Z0-9 and whitespace"
        )]
    )
    override fun update(ctx: Context, resourceId: String) {
        val userId: Int? = resourceId.toIntOrNull()
        if (userId == null) {
            response(ctx, 400, NullResponse(ResponseStatus.INVALID))
            return
        }
        val name = validateName(ctx)
        if (name == null || name == "") {
            response(ctx, 400, NullResponse(ResponseStatus.INVALID))
            return
        }

        var user: User
        try {
            user = users.findUser(userId)
        } catch (exc: EntityNotFoundException) {
            response(ctx, 404, AnyResponse(ResponseStatus.NOT_FOUND, object {
                val id = userId
            }))
            return
        }
        try {
            users.updateUser(user, name)
        } catch (exc: ExposedSQLException) {
            if (exc.cause is JdbcSQLIntegrityConstraintViolationException) {
                response(ctx, 409, NullResponse(ResponseStatus.INVALID))
                return
            }
            throw exc
        }
        response(ctx)
    }

    override fun delete(ctx: Context, resourceId: String) {
        val userId: Int? = resourceId.toIntOrNull()
        if (userId == null) {
            response(ctx, 400, NullResponse(ResponseStatus.INVALID))
            return
        }
        users.removeUser(userId)
        response(ctx)
    }

    private fun validateName(ctx: Context): String? {
        return ctx.queryParam<String>("name").validateName().getOrNull()
    }
}

fun Validator<String>.validateName(): Validator<String> {
    this.check({ it.length < 100 })
    this.check({ it.length > 1 })
    this.check({ it.matches(Regex("^[a-zA-Z0-9]+( +[a-zA-Z0-9]+)*")) })
    return this
}