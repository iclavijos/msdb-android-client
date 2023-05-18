package com.icesoft.msdb.android.model

data class Series(
    val id: Long? = 0,
    val name: String? = null,
    val shortname: String? = null,
    val relevance: Int? = 0,
    val logoUrl: String? = null,
    val organizer: String? = null
): BaseModel()
