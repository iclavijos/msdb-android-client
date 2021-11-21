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
    Float duration;

    public boolean isRally() {
        return Optional.ofNullable(rally).orElse(false);
    }
}
