package com.duluthtechnologies.ocpi.service.model.impl;

import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;

public class RegisteredEMSPChargingSessionImpl extends ChargingSessionImpl implements RegisteredEMSPChargingSession {

	private RegisteredEMSP registeredEMSP;

	@Override
	public RegisteredEMSP getRegisteredEMSP() {
		return registeredEMSP;
	}

	public void setRegisteredEMSP(RegisteredEMSP registeredEMSP) {
		this.registeredEMSP = registeredEMSP;
	}

}