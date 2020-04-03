package com.javalinist.logic

import com.javalinist.enums.OrderOptions
import com.javalinist.enums.SortByOptions
import com.javalinist.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.timerTask


open class Users {

    private val logger = Logger.getLogger(Users::class.java.name)

    var maxSize: Int = 1000

    private var cleanStarted: Boolean = false

    var cleanUpDelay: Long = 10000L
    var cleanUpPeriod: Long = 10000L

    private val sortByOptions: Map<SortByOptions, Expression<*>> = mapOf(
        SortByOptions.NAME to users_table.name,
        SortByOptions.ID to users_table.id)

    private fun size(): Int {
        var c: Int = 0
        transaction {
            c = DbUser.count().toInt()
        }
        return c
    }

    protected fun checkSize() {
        val size = size()
        if (size > maxSize) {
            transaction {
                repeat(size - maxSize) {
                    exec("SELECT MIN(id) FROM ${users_table.tableName}", {
                        if (it.next()) {
                            val minUserId = it.getInt(1)
                            logger.info("Cleaning user ${minUserId}")
                            if (minUserId > 0) {
                                removeUser(minUserId)
                            }
                        }
                    })
                }
            }
        }
    }

    protected fun getSortBy(sortBy: SortByOptions): Expression<*> {
        return sortByOptions.getOrDefault(sortBy, users_table.id)
    }

    fun sort(sortBy: SortByOptions, order: OrderOptions): List<User> {
        var users: MutableList<User> = mutableListOf()
        val sortColumn: Expression<*> = getSortBy(sortBy)
        val sortOrder: SortOrder = if (order == OrderOptions.DESC) SortOrder.DESC else SortOrder.ASC
        var query = users_table.selectAll()
        query.orderBy(sortColumn, sortOrder)

        transaction {
            query.forEach {
                users.add(User(it[users_table.id].value, it[users_table.name]))
            }
        }

        return users.toList()
    }

    private fun fixName(name: String): String {
        return name.capitalize()
    }

    open fun updateUser(user: User, name: String) {
        user.name = fixName(name)
        transaction {
            val u = DbUser.get(user.id)
            u.name = user.name
        }
    }

    fun findUser(userId: Int): User {
        lateinit var user: DbUser

        transaction {
            user = DbUser.get(userId)
        }
        return User(user.id.value, user.name)
    }

    fun findUser(name: String): User {
        val fname = fixName(name)
        var userId: Int = 0
        val query = DbUser.table.select {
            users_table.name eq fname
        }

        transaction {
            var res = query.first()
            userId = res[users_table.id].value
        }

        return User(userId, fname)
    }

    open fun removeUser(userId: Int) {
        try {
            transaction {
                val user = DbUser.get(userId)
                user.delete()
            }
        } catch (exc: EntityNotFoundException) {
        }
    }

    open fun createUser(name: String): User {
        lateinit var user: DbUser
        transaction {
            user = DbUser.new {
                this.name = fixName(name)
            }
        }

        return User(user.id.value, user.name)
    }

    @Synchronized()
    fun cleanUp() {
        if (cleanStarted) {
            return
        }
        cleanStarted = true

        GlobalScope.launch {
            val timer = Timer()
            timer.schedule(
                timerTask {
                    logger.info("Cleaning by checkSize")
                    checkSize()
                },
                cleanUpDelay, cleanUpPeriod
            )
        }
    }
}