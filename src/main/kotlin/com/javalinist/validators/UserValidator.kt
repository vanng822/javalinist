package com.javalinist.validators

import io.javalin.core.validation.Validator

class UserValidator(val name: String) {

    fun validate(): UserValidator {
        val v = Validator<UserValidator>(this)
        v.check({ it.name.length < 100 }, errorMessage = "name can not be longer than 99 characters")
        v.check({ it.name.length > 1 }, errorMessage = "name can not be less than 1 character")
        v.check(
            { it.name.matches(Regex("^[a-zA-Z0-9]+( +[a-zA-Z0-9]+)*")) },
            errorMessage = "Allowed characters a-zA-Z0-9 and whitespace"
        )
        return v.get()
    }
}