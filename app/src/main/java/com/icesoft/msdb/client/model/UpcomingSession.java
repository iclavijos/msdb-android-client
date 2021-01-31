package com.icesoft.msdb.client.model;

import java.util.List;

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
