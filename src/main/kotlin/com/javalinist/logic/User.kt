package com.javalinist.logic

import com.javalinist.models.User
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


open class Users {

    val maxSize: Int = 1000

    private fun size(): Int {
        var c: Int = 0
        transaction {
            c = DbUser.count().toInt()
        }
        return c
    }

    private fun checkSize() {
        if (size() > maxSize) {
            transaction {
                exec("SELECT MIN(id) FROM ${users_table.tableName}", {
                    if (it.next()) {
                        val minUserId = it.getInt(1)
                        if (minUserId > 0) {
                            DbUser.get(minUserId).delete()
                        }
                    }
                })
            }
        }
    }

    fun sort(sortBy: String?, order: String?): List<User> {
        var users: MutableList<User> = mutableListOf()
        transaction {
            var query = users_table.selectAll()
            // insane difficult; what did I miss
            if (sortBy == "name") {
                if (order == "desc") {
                    query.orderBy(users_table.name to SortOrder.DESC)
                } else {
                    query.orderBy(users_table.name to SortOrder.ASC)
                }
            } else {
                if (order == "desc") {
                    query.orderBy(users_table.id to SortOrder.DESC)
                } else {
                    query.orderBy(users_table.id to SortOrder.ASC)
                }
            }
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
        var user: DbUser? = null

        transaction {
            user = DbUser.get(userId)
        }
        return User(user!!.id.value, user!!.name)
    }

    fun findUser(name: String): User {
        val fname = fixName(name)
        var userId: Int = 0
        transaction {
            val query = DbUser.table.select {
                users_table.name eq fname
            }
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
        checkSize()
        var user: DbUser? = null
        transaction {
            user = DbUser.new {
                this.name = fixName(name)
            }
        }

        return User(user!!.id.value.toInt(), user!!.name)
    }
}