package com.icesoft.msdb.android.model

import com.icesoft.msdb.android.model.enums.DurationType
import com.icesoft.msdb.android.model.enums.SessionType

data class EventSession(
    val name: String? = null,
    val shortname: String? = null,
    val sessionStartTime: Long? = null,
    val sessionEndTime: Long? = null,
    val duration: Float? = null,
    val totalDuration: Float? = null,
    val durationType: DurationType? = null,
    val additionalLap: Boolean? = false,
    val sessionType: SessionType? = null,
    val rally: Boolean? = false,
    val raid: Boolean? = false,
    val cancelled: Boolean? = false
): BaseModel() {

    fun hasAdditionalLap(): Boolean {
        return additionalLap!!
    }

    fun isRally(): Boolean {
        return rally!!
    }

    fun isRaid(): Boolean {
        return raid!!
    }

    fun isCancelled(): Boolean {
        return cancelled!!
    }
}
