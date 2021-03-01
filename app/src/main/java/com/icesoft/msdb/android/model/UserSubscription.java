package com.icesoft.msdb.android.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSubscription extends BaseModel {

    private Long seriesEditionId;
    private String seriesEditionName;
    private String seriesLogo;
    private boolean practiceSessions;
    private boolean qualiSessions;
    private boolean races;
    private boolean fifteenMinWarning;
    private boolean oneHourWarning;
    private boolean threeHoursWarning;

    public UserSubscription(Long seriesEditionId, String seriesEditionName) {
        this.seriesEditionId = seriesEditionId;
        this.seriesEditionName = seriesEditionName;
    }

    public boolean isValid() {
        return
                (practiceSessions || qualiSessions || races) &&
                        (fifteenMinWarning || oneHourWarning || threeHoursWarning);
    }

    public void reset() {
        this.practiceSessions = false;
        this.qualiSessions = false;
        this.races = false;
        this.fifteenMinWarning = false;
        this.oneHourWarning = false;
        this.threeHoursWarning = false;
    }
}
