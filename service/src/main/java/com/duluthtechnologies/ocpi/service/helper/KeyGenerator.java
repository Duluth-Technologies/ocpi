package com.duluthtechnologies.ocpi.service.helper;

import org.apache.commons.lang3.RandomStringUtils;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;

public class KeyGenerator {

	public static String generateKey(Class type) {
		if (type.equals(RegisteredCPO.class)) {
			return "cpo_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else if (type.equals(RegisteredEMSP.class)) {
			return "emsp_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else if (type.equals(Location.class)) {
			return "loc_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else if (type.equals(Evse.class)) {
			return "evse_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else if (type.equals(Connector.class)) {
			return "con_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else {
			throw new RuntimeException();
		}
	}

}
