package com.duluthtechnologies.ocpi.core.model;

import jakarta.validation.constraints.NotNull;

public interface RegisteredEMSPChargingSession extends ChargingSession {

	@NotNull
	RegisteredEMSP getRegisteredEMSP();

}
