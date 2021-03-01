package com.icesoft.msdb.android.model;

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
}
