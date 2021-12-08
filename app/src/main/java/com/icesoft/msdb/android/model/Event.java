package com.icesoft.msdb.android.model;

import java.util.Optional;

import lombok.Data;

@Data
public class Event extends BaseModel {
    private Long id;
    private String name;
    private Boolean rally;
    private Boolean raid;

    public boolean isRally() {
        return Optional.ofNullable(rally).orElse(false);
    }

    public boolean isRaid() {
        return Optional.ofNullable(raid).orElse(false);
    }
}
