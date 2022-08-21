package com.icesoft.msdb.android.model;

import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UpcomingSession extends BaseModel {
    Long eventEditionId;
    String sessionName;
    String eventName;
    long sessionStartTime;
    long sessionEndTime;
    String racetrack;
    String seriesLogo;
    List<Long> seriesIds;
    List<Long> seriesEditionIds;
    Boolean rally;
    Boolean raid;
    Float duration;
    Float totalDuration;
    Boolean cancelled;

    public boolean isRally() {
        return Optional.ofNullable(rally).orElse(false);
    }
    public boolean isRaid() {
        return Optional.ofNullable(raid).orElse(false);
    }
    public boolean isCancelled() { return Optional.ofNullable(cancelled).orElse(false); }
}
