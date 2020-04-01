package com.javalinist.validators

import com.javalinist.enums.OrderOptions
import com.javalinist.enums.SortByOptions
import io.javalin.core.validation.JavalinValidation


fun registerValidators() {
    JavalinValidation.register(OrderOptions::class.java) {
        // support both lower and uppercase
        OrderOptions.valueOf(it.toUpperCase())
    }
    JavalinValidation.register(SortByOptions::class.java) {
        SortByOptions.valueOf(it.toUpperCase())
    }
}