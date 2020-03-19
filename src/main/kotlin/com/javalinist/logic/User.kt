package com.javalinist.logic

import com.javalinist.models.User

typealias Users = MutableList<User>

fun Users.findUser(userId: Int): User? {
    return this.find { user -> user.id == userId }
}

fun Users.findUser(name: String): User? {
    return this.find { user -> user.name == name }
}

fun Users.removeUser(userId: Int) {
    this.removeIf { user -> user.id == userId }
}

private fun Users.nextId(): Int {
    val lastUser = this.lastOrNull()
    if (lastUser == null) {
        return 1
    }
    return lastUser.id + 1
}

fun Users.createUser(name: String): Int {
    return synchronized(this) {
        val userId = nextId()
        val user = User(userId, name)
        this.add(user)
        userId
    }
}