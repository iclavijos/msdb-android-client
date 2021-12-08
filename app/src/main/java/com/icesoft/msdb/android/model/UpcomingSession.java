package com.icesoft.msdb.android.model;

import java.util.Optional;

import lombok.Data;

@Data
public class UpcomingSession extends BaseModel {
    Long eventEditionId;
    String sessionName;
    String eventName;
    long sessionStartTime;
    long sessionEndTime;
    String racetrack;
    String seriesLogo;
    Boolean rally;
    Boolean raid;
    Float duration;
    Float totalDuration;

    public boolean isRally() {
        return Optional.ofNullable(rally).orElse(false);
    }
    public boolean isRaid() {
        return Optional.ofNullable(raid).orElse(false);
    }
}
