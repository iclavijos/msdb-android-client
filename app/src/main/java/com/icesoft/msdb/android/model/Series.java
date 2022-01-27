package com.icesoft.msdb.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import lombok.Data;

@Data
public class Series extends BaseModel {
    Long id;
    String name;
    String logoUrl;
    String organizer;

//    @JsonProperty("series")
//    private void unpackNested(Map<String,Object> series) {
//        this.name = (String)series.get("name");
//        this.seriesLogo = (String)series.get("logoUrl");
//        this.organizer = (String)series.get("organizer");
//    }
}
