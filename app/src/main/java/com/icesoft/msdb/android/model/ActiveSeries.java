package com.icesoft.msdb.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import lombok.Data;

@Data
public class ActiveSeries extends BaseModel {
    Long id;
    String editionName;
    String seriesLogo;

    @JsonProperty("series")
    private void unpackNested(Map<String,Object> series) {
        this.seriesLogo = (String)series.get("logoUrl");
    }
}
