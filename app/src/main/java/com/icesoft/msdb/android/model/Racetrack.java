package com.icesoft.msdb.android.model;

import lombok.Data;

@Data
public class Racetrack extends BaseModel {

    private Long id;
    private String name;
    private String location;
    private String countryCode;
    private String logoUrl;
}
