package com.duluthtechnologies.ocpi.test.persistence;

import static org.assertj.core.api.Assertions.within;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Format;
import com.duluthtechnologies.ocpi.core.model.Connector.PowerType;
import com.duluthtechnologies.ocpi.core.model.Connector.Type;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;
import com.duluthtechnologies.ocpi.core.store.ChargingSessionStore;
import com.duluthtechnologies.ocpi.core.store.ConnectorStore;
import com.duluthtechnologies.ocpi.core.store.EvseStore;
import com.duluthtechnologies.ocpi.core.store.LocationStore;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.persistence.entity.ConnectorEntity;
import com.duluthtechnologies.ocpi.persistence.entity.EvseEntity;
import com.duluthtechnologies.ocpi.persistence.entity.LocationEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPChargingSessionEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPEntity;

class ChargingSessionStoreTest extends AbstractTest {

	@Autowired
	RegisteredOperatorStore registeredOperatorStore;

	@Autowired
	ChargingSessionStore chargingSessionStore;

	@Autowired
	LocationStore locationStore;

	@Autowired
	EvseStore evseStore;

	@Autowired
	ConnectorStore connectorStore;

	@Test
	void testCreateThenUpate() {
		String partyId = RandomStringUtils.random(3, true, false);
		String token = RandomStringUtils.random(16, true, false);
		RegisteredEMSPEntity entity = new RegisteredEMSPEntity();
		entity.setKey("emsp_" + partyId.toUpperCase());
		entity.setName(partyId);
		entity.setCountryCode("FR");
		entity.setPartyId(partyId.toUpperCase());
		entity.setIncomingToken(token);
		RegisteredEMSP registeredEMSP = (RegisteredEMSP) registeredOperatorStore.create(entity);

		LocationEntity locationEntity = new LocationEntity();
		String locationKey = UUID.randomUUID().toString();
		locationEntity.setKey(locationKey);
		locationEntity.setOcpiId(UUID.randomUUID().toString());
		locationEntity.setAddress(RandomStringUtils.random(16, true, false));
		locationEntity.setCity(RandomStringUtils.random(8, true, false));
		locationEntity.setZipCode(RandomStringUtils.random(5, false, true));
		locationEntity.setCountryCode(RandomStringUtils.random(3, true, false));
		Location location = locationStore.createLocation(locationEntity);

		EvseEntity evseEntity = new EvseEntity();
		evseEntity.setKey(UUID.randomUUID().toString());
		evseEntity.setOcpiId(UUID.randomUUID().toString());
		evseEntity.setEvseId(UUID.randomUUID().toString());
		Evse evse = evseStore.createEVSE(location.getKey(), evseEntity);

		ConnectorEntity connectorEntity = new ConnectorEntity();
		connectorEntity.setKey(UUID.randomUUID().toString());
		connectorEntity.setEvse(evse);
		connectorEntity.setType(Type.CHADEMO);
		connectorEntity.setFormat(Format.CABLE);
		connectorEntity.setPowerType(PowerType.DC);
		connectorEntity.setConnectorId("1");
		Connector connector = connectorStore.createConnector(evse.getKey(), connectorEntity);

		RegisteredEMSPChargingSessionEntity registeredEMSPChargingSessionEntity = new RegisteredEMSPChargingSessionEntity();
		String chargingSessionKey = RandomStringUtils.random(16, true, false);
		registeredEMSPChargingSessionEntity.setKey(chargingSessionKey);
		String ocpiId = RandomStringUtils.random(16, true, false);
		registeredEMSPChargingSessionEntity.setOcpiId(ocpiId);
		registeredEMSPChargingSessionEntity.setRegisteredEMSP(registeredEMSP);
		registeredEMSPChargingSessionEntity.setConnector(connector);
		RegisteredEMSPChargingSession registeredEMSPChargingSession = chargingSessionStore
				.create(registeredEMSPChargingSessionEntity);

		Assertions.assertThat(registeredEMSPChargingSession.getKey()).isEqualTo(chargingSessionKey);
		Assertions.assertThat(registeredEMSPChargingSession.getOcpiId()).isEqualTo(ocpiId);
		Assertions.assertThat(registeredEMSPChargingSession.getConnector().getKey()).isEqualTo(connector.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getRegisteredEMSP().getKey())
				.isEqualTo(registeredEMSP.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getStartDate()).isNull();
		Assertions.assertThat(registeredEMSPChargingSession.getStopDate()).isNull();

		registeredEMSPChargingSession = (RegisteredEMSPChargingSession) chargingSessionStore
				.findByKey(chargingSessionKey).get();

		Assertions.assertThat(registeredEMSPChargingSession.getKey()).isEqualTo(chargingSessionKey);
		Assertions.assertThat(registeredEMSPChargingSession.getOcpiId()).isEqualTo(ocpiId);
		Assertions.assertThat(registeredEMSPChargingSession.getConnector().getKey()).isEqualTo(connector.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getRegisteredEMSP().getKey())
				.isEqualTo(registeredEMSP.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getStartDate()).isNull();
		Assertions.assertThat(registeredEMSPChargingSession.getStopDate()).isNull();

		registeredEMSPChargingSessionEntity = new RegisteredEMSPChargingSessionEntity();
		registeredEMSPChargingSessionEntity.setKey(chargingSessionKey);
		registeredEMSPChargingSessionEntity.setOcpiId(ocpiId);
		registeredEMSPChargingSessionEntity.setRegisteredEMSP(registeredEMSP);
		registeredEMSPChargingSessionEntity.setConnector(connector);
		Instant startDate = Instant.now();
		registeredEMSPChargingSessionEntity.setStartDate(startDate);
		registeredEMSPChargingSession = (RegisteredEMSPChargingSession) chargingSessionStore
				.update(registeredEMSPChargingSessionEntity);

		Assertions.assertThat(registeredEMSPChargingSession.getKey()).isEqualTo(chargingSessionKey);
		Assertions.assertThat(registeredEMSPChargingSession.getOcpiId()).isEqualTo(ocpiId);
		Assertions.assertThat(registeredEMSPChargingSession.getConnector().getKey()).isEqualTo(connector.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getRegisteredEMSP().getKey())
				.isEqualTo(registeredEMSP.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getStartDate()).isCloseTo(startDate,
				within(1, ChronoUnit.MILLIS));
		Assertions.assertThat(registeredEMSPChargingSession.getStopDate()).isNull();

		registeredEMSPChargingSession = (RegisteredEMSPChargingSession) chargingSessionStore
				.findByKey(chargingSessionKey).get();

		Assertions.assertThat(registeredEMSPChargingSession.getKey()).isEqualTo(chargingSessionKey);
		Assertions.assertThat(registeredEMSPChargingSession.getOcpiId()).isEqualTo(ocpiId);
		Assertions.assertThat(registeredEMSPChargingSession.getConnector().getKey()).isEqualTo(connector.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getRegisteredEMSP().getKey())
				.isEqualTo(registeredEMSP.getKey());
		Assertions.assertThat(registeredEMSPChargingSession.getStartDate()).isCloseTo(startDate,
				within(1, ChronoUnit.MILLIS));
		Assertions.assertThat(registeredEMSPChargingSession.getStopDate()).isNull();
	}

}
