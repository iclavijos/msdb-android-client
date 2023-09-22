package com.icesoft.msdb.android.model

import java.time.LocalDate

data class EventEdition(
    val id: Long? = null,
    val longEventName: String? = null,
    val event: Event? = null,
    private val eventDate: Array<Int?>?,
    val trackLayout: RacetrackLayout? = null,
    val posterUrl: String? = null,
    val multidriver: Boolean? = false,
    val location: String? = null,
    val status: String? = null
): BaseModel() {

    constructor(): this(null, null,null, null, null, null, false, null, null)

    fun getEventDate(): LocalDate? {
        return LocalDate.of(
            eventDate!![0]!!,
            eventDate[1]!!,
            eventDate[2]!!
        )
    }

    fun isRally(): Boolean {
        return event!!.isRally()
    }

    fun isRaid(): Boolean {
        return event!!.isRaid()
    }
}
