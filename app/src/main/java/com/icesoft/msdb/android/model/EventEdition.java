package com.icesoft.msdb.android.model;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import lombok.Data;

@Data
public class EventEdition extends BaseModel {

    private Long id;
    private Event event;
    private Integer[] eventDate;
    private RacetrackLayout trackLayout;
    private String posterUrl;
    private Boolean multidriver;
    private String location;

    public LocalDate getEventDate() {
        return LocalDate.of(eventDate[0] , eventDate[1], eventDate[2]);
    }

    public boolean isRally() {
        return event.isRally();
    }

    public boolean isRaid() {
        return event.isRaid();
    }
}
