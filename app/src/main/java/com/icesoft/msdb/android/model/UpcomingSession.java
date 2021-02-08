package com.icesoft.msdb.android.model;

import lombok.Data;

@Data
public class UpcomingSession {
    String sessionName;
    String eventName;
    long sessionStartTime;
    long sessionEndTime;
    String racetrack;
    String seriesLogo;
}
