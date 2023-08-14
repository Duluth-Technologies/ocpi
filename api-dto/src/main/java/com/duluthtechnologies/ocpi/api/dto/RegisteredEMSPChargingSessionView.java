package com.duluthtechnologies.ocpi.api.dto;

import jakarta.validation.constraints.NotEmpty;

public class RegisteredEMSPChargingSessionView extends ChargingSessionView {

	@NotEmpty
	String registeredEmspKey;

	public String getRegisteredEmspKey() {
		return registeredEmspKey;
	}

	public void setRegisteredEmspKey(String registeredEmspKey) {
		this.registeredEmspKey = registeredEmspKey;
	}

}
