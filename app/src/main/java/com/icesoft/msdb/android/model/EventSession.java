package com.icesoft.msdb.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EventSession extends BaseModel {

    private String name;
    private String shortname;
    @JsonProperty("sessionStartTime")
    private Long startTime;
    @JsonProperty("sessionEndTime")
    private Long endTime;
    private Integer duration;
    private Integer durationType;
    private boolean additionalLap;
}
