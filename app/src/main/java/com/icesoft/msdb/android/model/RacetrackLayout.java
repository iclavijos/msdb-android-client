package com.icesoft.msdb.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RacetrackLayout extends BaseModel {

    private Long id;
    private String name;
    private Integer length;
    private Integer yearFirstUse;
    private Boolean active;
    @JsonProperty("layoutImageUrl")
    private String layoutUrl;
    private Racetrack racetrack;
}
