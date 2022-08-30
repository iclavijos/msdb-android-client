package com.icesoft.msdb.android.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum SessionType {
	PRACTICE,
	QUALIFYING,
	RACE,
	QUALIFYING_RACE,
    STAGE;

	@JsonCreator
	public static SessionType fromString(String value) {
		if (value.equals("qualifyingRace")) {
			return QUALIFYING_RACE;
		}
		return SessionType.valueOf(value.toUpperCase(Locale.ROOT));
	}
}
