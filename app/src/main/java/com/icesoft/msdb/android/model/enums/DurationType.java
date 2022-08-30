package com.icesoft.msdb.android.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum DurationType {
	MINUTES,
	HOURS,
	KMS,
	MILES,
	LAPS;

	@JsonCreator
	public static DurationType fromString(String value) {
		return DurationType.valueOf(value.toUpperCase(Locale.ROOT));
	}
}
