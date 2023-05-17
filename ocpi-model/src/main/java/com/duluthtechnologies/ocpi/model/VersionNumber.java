package com.duluthtechnologies.ocpi.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VersionNumber {

	V2_0_0("2.0.0"), V2_1("2.1"), V2_1_1("2.1.1"), V2_2_1("2.2.1");

	@JsonValue
	public final String value;

	private VersionNumber(String value) {
		this.value = value;
	}

}
