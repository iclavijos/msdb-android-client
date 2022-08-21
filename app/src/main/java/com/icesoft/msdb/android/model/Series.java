package com.icesoft.msdb.android.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Series extends BaseModel {
    Long id;
    String name;
    String shortname;
    Integer relevance;
    String logoUrl;
    String organizer;

}
