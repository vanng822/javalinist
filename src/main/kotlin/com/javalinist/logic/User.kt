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
    removeIf {
        user -> user.id == userId
    }
}

fun Users.nextId(): Int {
    val lastUser = this.lastOrNull()
    if (lastUser == null) {
        return 1
    }
    return lastUser.id + 1
}