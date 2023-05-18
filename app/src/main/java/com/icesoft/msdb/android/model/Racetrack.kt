package com.icesoft.msdb.android.model

data class Racetrack(
    val id: Long? = 0,
    val name: String? = null,
    val location: String? = null,
    val countryCode: String? = null,
    val logoUrl: String? = null
): BaseModel()
