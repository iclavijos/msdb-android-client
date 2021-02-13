package com.icesoft.msdb.android.model;

import lombok.Data;

@Data
public class UserSubscription {

    private int seriesEditionId;
    private boolean practiceSessions;
    private boolean qualiSessions;
    private boolean races;
    private boolean fifteenMinWarning;
    private boolean oneHourWarning;
    private boolean threeHoursWarning;

    boolean isValid() {
        return
                (practiceSessions || qualiSessions || races) &&
                        (fifteenMinWarning || oneHourWarning || threeHoursWarning);
    }

    void reset() {
        this.practiceSessions = false;
        this.qualiSessions = false;
        this.races = false;
        this.fifteenMinWarning = false;
        this.oneHourWarning = false;
        this.threeHoursWarning = false;
    }
}
