package com.javalinist.logic

import com.javalinist.models.User

class Users {
    var users: MutableList<User> = mutableListOf()
    var nextId: Int = 1

    // Don't mention performance!!
    fun sortByDescending(sortBy: String): MutableList<User> {
        if (sortBy == "name") {
            users.sortByDescending { user -> user.name }
        } else {
            users.sortByDescending { user -> user.id }
        }
        return users
    }

    private fun fixName(name: String): String {
        return name.capitalize()
    }

    fun updateUser(user: User, name: String) {
        user.name = fixName(name)
    }

    fun findUser(userId: Int): User? {
        return users.find { user -> user.id == userId }
    }

    fun findUser(name: String): User? {
        val fname = fixName(name)
        return users.find { user -> user.name == fname }
    }

    fun removeUser(userId: Int) {
        users.removeIf { user -> user.id == userId }
    }

    fun createUser(name: String): Int {
        return synchronized(this.nextId) {
            val userId = this.nextId++
            val user = User(userId, fixName(name))
            users.add(user)
            userId
        }
    }
}
