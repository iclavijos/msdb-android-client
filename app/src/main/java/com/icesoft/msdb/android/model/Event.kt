package com.icesoft.msdb.android.model

import java.util.*

data class Event(
    val id: Long? = 0,
    val name: String? = null,
    val rally: Boolean? = false,
    val raid: Boolean? = false
): BaseModel() {
    fun isRally(): Boolean {
        return Optional.ofNullable(rally).orElse(false)
    }

    fun isRaid(): Boolean {
        return Optional.ofNullable(raid).orElse(false)
    }
}
