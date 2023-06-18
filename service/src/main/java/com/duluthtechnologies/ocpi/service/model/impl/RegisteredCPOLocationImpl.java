package com.duluthtechnologies.ocpi.service.model.impl;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;

public class RegisteredCPOLocationImpl extends LocationImpl implements RegisteredCPOLocation {

	private RegisteredCPO registeredCPO;

	@Override
	public RegisteredCPO getRegisteredCPO() {
		return registeredCPO;
	}

	public void setRegisteredCPO(RegisteredCPO registeredCPO) {
		this.registeredCPO = registeredCPO;
	}

}