package com.duluthtechnologies.ocpi.test.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Format;
import com.duluthtechnologies.ocpi.core.model.Connector.PowerType;
import com.duluthtechnologies.ocpi.core.model.Connector.Type;
import com.duluthtechnologies.ocpi.core.service.LocationService;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.EvseForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.LocationForm;

class LocationServiceTest extends AbstractTest {

	private static final Logger LOG = LoggerFactory.getLogger(LocationServiceTest.class);

	@Autowired
	private LocationService locationService;

	@Test
	void testCreateLocation() {
		ConnectorForm connectorForm1 = new ConnectorForm("1", Type.IEC_62196_T2_COMBO, Format.CABLE, PowerType.DC, 1000,
				100, Connector.Status.AVAILABLE);

		ConnectorForm connectorForm2 = new ConnectorForm("2", Type.IEC_62196_T2_COMBO, Format.CABLE, PowerType.DC, 1000,
				100, Connector.Status.AVAILABLE);

		EvseForm evseForm1 = new EvseForm(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				List.of(connectorForm1, connectorForm2));

		ConnectorForm connectorForm3 = new ConnectorForm("1", Type.IEC_62196_T2_COMBO, Format.CABLE, PowerType.DC, 1000,
				100, Connector.Status.AVAILABLE);

		ConnectorForm connectorForm4 = new ConnectorForm("2", Type.IEC_62196_T2_COMBO, Format.CABLE, PowerType.DC, 1000,
				100, Connector.Status.AVAILABLE);

		EvseForm evseForm2 = new EvseForm(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
				List.of(connectorForm3, connectorForm4));

		LocationForm locationForm = new LocationForm(UUID.randomUUID().toString(),
				RandomStringUtils.random(16, true, false), RandomStringUtils.random(16, true, false),
				RandomStringUtils.random(16, true, false), RandomStringUtils.random(5, false, true), "FRA",
				RandomUtils.nextDouble(40, 50), RandomUtils.nextDouble(0, 10), List.of(evseForm1, evseForm2));
		
		locationService.createLocation(locationForm);

	}

}
