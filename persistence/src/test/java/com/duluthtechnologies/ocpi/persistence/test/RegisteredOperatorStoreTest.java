package com.duluthtechnologies.ocpi.persistence.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPEntity;
import com.duluthtechnologies.ocpi.persistence.entity.v211.RegisteredEMSPV211Entity;
import com.duluthtechnologies.ocpi.persistence.mapper.RegisteredOperatorEntityMapper;
import com.duluthtechnologies.ocpi.persistence.test.config.SpringBootTestConfiguration;

@SpringBootTest(classes = SpringBootTestConfiguration.class)
class RegisteredOperatorStoreTest {

	@Autowired
	RegisteredOperatorStore registeredOperatorStore;

	@Autowired
	RegisteredOperatorEntityMapper registeredOperatorEntityMapper;

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
	void testCreateThenUpdateAnotherType() {
		RegisteredEMSPEntity entity = new RegisteredEMSPEntity();
		entity.setKey("emsp_KEY");
		entity.setName("name");
		entity.setCountryCode("FR");
		entity.setPartyId("AAA");
		RegisteredOperator savedRegisteredOperator = registeredOperatorStore.create(entity);
		Assertions.assertThat(savedRegisteredOperator).isInstanceOf(RegisteredEMSPEntity.class);

		RegisteredEMSPV211Entity entityV211 = new RegisteredEMSPV211Entity();
		entityV211.setKey("emsp_KEY");
		entityV211.setName("name");
		entityV211.setCountryCode("FR");
		entityV211.setPartyId("AAA");
		RegisteredOperator updatedRegisteredOperator = registeredOperatorStore.update(entityV211);
		Assertions.assertThat(updatedRegisteredOperator).isInstanceOf(RegisteredEMSPV211Entity.class);
	}

}
