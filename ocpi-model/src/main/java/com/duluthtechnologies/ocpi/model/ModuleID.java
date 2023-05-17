package com.duluthtechnologies.ocpi.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModuleID {

	CDRs("cdrs"), Commands("commands"), CredentialsRegistration("credentials"), Locations("locations"),
	Sessions("sessions"), Tariffs("tariffs"), Tokens("tokens");

	@JsonValue
	public final String value;

	private ModuleID(String value) {
		this.value = value;
	}
}
