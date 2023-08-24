package com.duluthtechnologies.ocpi.test.persistence;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredCPOEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPEntity;
import com.duluthtechnologies.ocpi.persistence.entity.v211.RegisteredCPOV211Entity;
import com.duluthtechnologies.ocpi.persistence.entity.v211.RegisteredEMSPV211Entity;

class RegisteredOperatorStoreTest extends AbstractTest {

	@Autowired
	RegisteredOperatorStore registeredOperatorStore;

	@Test
	void testCreateThenFindByToken() {
		String partyId = RandomStringUtils.random(3, true, false);
		String token = RandomStringUtils.random(16, true, false);
		RegisteredEMSPEntity entity = new RegisteredEMSPEntity();
		entity.setKey("emsp_" + partyId.toUpperCase());
		entity.setName(partyId);
		entity.setCountryCode("FR");
		entity.setPartyId(partyId.toUpperCase());
		entity.setIncomingToken(token);
		registeredOperatorStore.create(entity);

		Assertions.assertThat(registeredOperatorStore.findByIncomingToken(token)).isPresent();
	}

	@Test
	void testCreateThenUpdateUpgradingRegisteredEMSP() {
		RegisteredEMSPEntity entity = new RegisteredEMSPEntity();
		entity.setKey("emsp_KEY");
		entity.setName("name");
		entity.setCountryCode("FR");
		entity.setPartyId("AAA");

		RegisteredOperator savedRegisteredOperator = registeredOperatorStore.create(entity);

		Assertions.assertThat(savedRegisteredOperator).isInstanceOf(RegisteredEMSPEntity.class);
		Assertions.assertThat(savedRegisteredOperator.getKey()).isEqualTo("emsp_KEY");
		Assertions.assertThat(savedRegisteredOperator.getName()).isEqualTo("name");
		Assertions.assertThat(savedRegisteredOperator.getCountryCode()).isEqualTo("FR");
		Assertions.assertThat(savedRegisteredOperator.getPartyId()).isEqualTo("AAA");

		RegisteredEMSPV211Entity entityV211 = new RegisteredEMSPV211Entity();
		entityV211.setKey("emsp_KEY");
		entityV211.setName("name");
		entityV211.setCountryCode("FR");
		entityV211.setPartyId("AAA");
		entityV211.setLocationsUrl("http://location-url");

		RegisteredOperator updatedRegisteredOperator = registeredOperatorStore.update(entityV211);

		Assertions.assertThat(updatedRegisteredOperator).isInstanceOf(RegisteredEMSPV211Entity.class);
		Assertions.assertThat(updatedRegisteredOperator.getKey()).isEqualTo("emsp_KEY");
		Assertions.assertThat(updatedRegisteredOperator.getName()).isEqualTo("name");
		Assertions.assertThat(updatedRegisteredOperator.getCountryCode()).isEqualTo("FR");
		Assertions.assertThat(updatedRegisteredOperator.getPartyId()).isEqualTo("AAA");
		Assertions.assertThat(((RegisteredEMSPV211Entity) updatedRegisteredOperator).getLocationsUrl())
				.isEqualTo("http://location-url");
	}

	@Test
	void testCreateThenUpdateUpgradingRegisteredCPO() {
		RegisteredCPOEntity entity = new RegisteredCPOEntity();
		entity.setKey("cpo_KEY");
		entity.setName("name");
		entity.setCountryCode("FR");
		entity.setPartyId("BBB");

		RegisteredOperator savedRegisteredOperator = registeredOperatorStore.create(entity);

		Assertions.assertThat(savedRegisteredOperator).isInstanceOf(RegisteredCPOEntity.class);
		Assertions.assertThat(savedRegisteredOperator.getKey()).isEqualTo("cpo_KEY");
		Assertions.assertThat(savedRegisteredOperator.getName()).isEqualTo("name");
		Assertions.assertThat(savedRegisteredOperator.getCountryCode()).isEqualTo("FR");
		Assertions.assertThat(savedRegisteredOperator.getPartyId()).isEqualTo("BBB");

		RegisteredCPOV211Entity entityV211 = new RegisteredCPOV211Entity();
		entityV211.setKey("cpo_KEY");
		entityV211.setName("name");
		entityV211.setCountryCode("FR");
		entityV211.setPartyId("BBB");
		entityV211.setLocationsUrl("http://location-url");

		RegisteredOperator updatedRegisteredOperator = registeredOperatorStore.update(entityV211);

		Assertions.assertThat(updatedRegisteredOperator).isInstanceOf(RegisteredCPOV211Entity.class);
		Assertions.assertThat(updatedRegisteredOperator.getKey()).isEqualTo("cpo_KEY");
		Assertions.assertThat(updatedRegisteredOperator.getName()).isEqualTo("name");
		Assertions.assertThat(updatedRegisteredOperator.getCountryCode()).isEqualTo("FR");
		Assertions.assertThat(updatedRegisteredOperator.getPartyId()).isEqualTo("BBB");
		Assertions.assertThat(((RegisteredCPOV211Entity) updatedRegisteredOperator).getLocationsUrl())
				.isEqualTo("http://location-url");
	}

}
