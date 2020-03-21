package com.javalinist.models

import com.javalinist.enums.UserEventType

data class UserEvent(val type: UserEventType, val user: User)