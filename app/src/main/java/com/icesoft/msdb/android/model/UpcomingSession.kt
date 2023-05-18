package com.icesoft.msdb.android.model

import java.util.*

data class UpcomingSession(
    val eventEditionId: Long? = null,
    val sessionName: String? = null,
    val eventName: String? = null,
    val sessionStartTime: Long = 0,
    val sessionEndTime: Long = 0,
    val racetrack: String? = null,
    val seriesLogo: String? = null,
    val seriesIds: List<Long?>? = null,
    val seriesEditionIds: List<Long?>? = null,
    val rally: Boolean? = null,
    val raid: Boolean? = null,
    val duration: Float? = null,
    val totalDuration: Float? = null,
    val cancelled: Boolean? = null
): BaseModel() {
    fun isRally(): Boolean {
        return Optional.ofNullable(rally).orElse(false)
    }

    fun isRaid(): Boolean {
        return Optional.ofNullable(raid).orElse(false)
    }

    fun isCancelled(): Boolean {
        return Optional.ofNullable(cancelled).orElse(false)
    }
}
