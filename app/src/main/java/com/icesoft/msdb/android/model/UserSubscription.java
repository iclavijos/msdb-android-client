package com.icesoft.msdb.android.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserSubscription extends BaseModel {

    private Long seriesId;
    private String seriesName;
    private String seriesLogo;
    private boolean practiceSessions;
    private boolean qualiSessions;
    private boolean races;
    private boolean fifteenMinWarning;
    private boolean oneHourWarning;
    private boolean threeHoursWarning;

    public UserSubscription(Long seriesId, String seriesName, String seriesLogo) {
        this.seriesId = seriesId;
        this.seriesName = seriesName;
        this.seriesLogo = seriesLogo;
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
