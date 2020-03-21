package com.javalinist.models

import com.javalinist.enums.UserEventType

data class UserEvent(val eventType: UserEventType, val user: User)