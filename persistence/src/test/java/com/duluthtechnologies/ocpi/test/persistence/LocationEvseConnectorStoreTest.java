package com.duluthtechnologies.ocpi.test.persistence;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.store.ConnectorStore;
import com.duluthtechnologies.ocpi.core.store.EvseStore;
import com.duluthtechnologies.ocpi.core.store.LocationStore;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.persistence.entity.CPOLocationEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredCPOEntity;

class LocationEvseConnectorStoreTest extends AbstractTest {

	@Autowired
	RegisteredOperatorStore registeredOperatorStore;

	@Autowired
	LocationStore locationStore;

	@Autowired
	EvseStore evseStore;

	@Autowired
	ConnectorStore connectorStore;

	@Test
	void testCreateThenFindByToken() {
		String partyId = RandomStringUtils.random(3, true, false);
		String token = RandomStringUtils.random(16, true, false);
		RegisteredCPOEntity registeredCPOEntity = new RegisteredCPOEntity();
		registeredCPOEntity.setKey("cpo_" + partyId.toUpperCase());
		registeredCPOEntity.setName(partyId);
		registeredCPOEntity.setCountryCode("FR");
		registeredCPOEntity.setPartyId(partyId.toUpperCase());
		registeredCPOEntity.setIncomingToken(token);
		RegisteredCPO registeredCPO = (RegisteredCPO) registeredOperatorStore.create(registeredCPOEntity);

		CPOLocationEntity cpoLocationEntity = new CPOLocationEntity();
		String locationKey = UUID.randomUUID().toString();
		cpoLocationEntity.setKey(locationKey);
		cpoLocationEntity.setOcpiId(UUID.randomUUID().toString());
		cpoLocationEntity.setAddress(RandomStringUtils.random(16, true, false));
		cpoLocationEntity.setCity(RandomStringUtils.random(8, true, false));
		cpoLocationEntity.setZipCode(RandomStringUtils.random(5, false, true));
		cpoLocationEntity.setCountryCode(RandomStringUtils.random(3, true, false));
		Location location = locationStore.createRegisteredCPOLocation(cpoLocationEntity, registeredCPO.getKey());
		Assertions.assertThat(location.getKey()).isEqualTo(locationKey);

		Optional<RegisteredCPOLocation> optionalLocation = locationStore.findByCountryCodeAndPartyIdAndOcpiId("FR",
				partyId.toUpperCase(), location.getOcpiId());
		Assertions.assertThat(optionalLocation).isPresent();
		Assertions.assertThat(optionalLocation.get().getKey()).isEqualTo(locationKey);
	}

}
