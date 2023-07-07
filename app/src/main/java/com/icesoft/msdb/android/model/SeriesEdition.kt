package com.icesoft.msdb.android.model

data class SeriesEdition(
    val id: Long? = null,
    val editionName: String? = null,
    val periodEnd: String? = null,
    val logoUrl: String? = null
) {
    override fun toString(): String {
        return editionName!!
    }
}
