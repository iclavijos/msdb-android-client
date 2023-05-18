package com.icesoft.msdb.android.model

data class UserSubscription(
    var seriesId: Long? = null,
    var seriesName: String? = null,
    var seriesLogo: String? = null,
    var practiceSessions: Boolean? = false,
    var qualiSessions: Boolean? = false,
    var races: Boolean? = false,
    var fifteenMinWarning: Boolean? = false,
    var oneHourWarning: Boolean? = false,
    var threeHoursWarning: Boolean? = false
): BaseModel() {

    constructor(
        seriesId: Long? = null,
        seriesName: String? = null,
        seriesLogo: String? = null
    ): this(seriesId, seriesName, seriesLogo, false, false, false, false, false, false)

    fun isValid(): Boolean {
        return (practiceSessions!! || qualiSessions!! || races!!) &&
                (fifteenMinWarning!! || oneHourWarning!! || threeHoursWarning!!)
    }

    fun isPracticeSessions(): Boolean {
        return practiceSessions!!
    }

    fun isQualiSessions(): Boolean {
        return qualiSessions!!
    }

    fun isRaces(): Boolean {
        return races!!
    }

    fun isFifteenMinWarning(): Boolean {
        return fifteenMinWarning!!
    }

    fun isOneHourWarning(): Boolean {
        return oneHourWarning!!
    }

    fun isThreeHoursWarning(): Boolean {
        return threeHoursWarning!!
    }

    fun reset() {
        practiceSessions = false
        qualiSessions = false
        races = false
        fifteenMinWarning = false
        oneHourWarning = false
        threeHoursWarning = false
    }
}
