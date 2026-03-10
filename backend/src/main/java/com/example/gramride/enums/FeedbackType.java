package com.example.gramride.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FeedbackType {
	NORMAL, RASH_DRIVE, VERY_RASH_DRIVE, POLITE_DRIVING, ON_TIME;

	@JsonCreator
	public static FeedbackType fromString(String key) {
		return FeedbackType.valueOf(key.toUpperCase().replaceAll("[-\\s]", "_"));
	}
}
