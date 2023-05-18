package com.icesoft.msdb.android.model

data class RacetrackLayout(
    val id: Long? = null,
    val name: String? = null,
    val length: Int? = null,
    val yearFirstUse: Int? = null,
    val active: Boolean? = null,
    val layoutImageUrl: String? = null,
    val racetrack: Racetrack? = null
): BaseModel()
