package com.icesoft.msdb.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icesoft.msdb.android.model.enums.DurationType;
import com.icesoft.msdb.android.model.enums.SessionType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class EventSession extends BaseModel {

    private String name;
    private String shortname;
    @JsonProperty("sessionStartTime")
    private Long startTime;
    @JsonProperty("sessionEndTime")
    private Long endTime;
    private Float duration;
    private Float totalDuration;
    private DurationType durationType;
    private boolean additionalLap;
    private SessionType sessionType;
    private boolean rally;
    private boolean raid;
    private boolean cancelled;
}
