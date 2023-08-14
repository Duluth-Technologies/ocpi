package com.duluthtechnologies.ocpi.service.helper;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;

public class KeyGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(KeyGenerator.class);

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
		} else if (type.equals(ChargingSession.class)) {
			return "cs_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else if (type.equals(RegisteredEMSPChargingSession.class)) {
			return "recs_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else {
			String message = "Type [%s] not handled.".formatted(type.toString());
			LOG.error(message);
			throw new RuntimeException(message);
		}
	}

}
