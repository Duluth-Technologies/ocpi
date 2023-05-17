package com.duluthtechnologies.ocpi.test.integration.emsp;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.api.dto.CPORegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.EMSPRegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.RegisteredCPOView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPView;
import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredCPOV211View;
import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredEMSPV211View;
import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.CPOTestInstance;

class EMSPIntegrationTest extends AbstractEMSPTest {

	private static final Logger LOG = LoggerFactory.getLogger(EMSPIntegrationTest.class);

	private RestTemplate restTemplate = new RestTemplate();

	private CPOTestInstance cpoTestInstance;

	@BeforeEach
	void initialize() {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		cpoTestInstance = ocpiContainerProvider.createCPOContainer(network, "FR", partyId);
	}

	@AfterEach
	void teardown() {
		emspTestInstance.teardown();
	}

	@Test
	void testPerformHandshake() {
		LOG.info("Registering CPO into EMSP...");
		String cpoKey = "cpo_" + cpoTestInstance.getCountryCode() + cpoTestInstance.getPartyId();
		CPORegistrationForm cpoRegistrationForm = new CPORegistrationForm();
		cpoRegistrationForm.setCountryCode(cpoTestInstance.getCountryCode());
		cpoRegistrationForm.setPartyId(cpoTestInstance.getPartyId());
		cpoRegistrationForm.setKey(cpoKey);
		cpoRegistrationForm.setName(cpoTestInstance.getPartyId());
		cpoRegistrationForm.setVersionUrl(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/versions");
		cpoRegistrationForm.setOutgoingToken("token");
		RegisteredCPOView registeredCPOView = restTemplate
				.postForEntity(emspTestInstance.getExternalUrl() + "/api/admin/cpo", cpoRegistrationForm,
						RegisteredCPOView.class)
				.getBody();

		LOG.info("Checking registered CPO...");
		Assertions.assertThat(registeredCPOView.getKey()).isEqualTo(cpoKey);
		Assertions.assertThat(registeredCPOView.getCountryCode()).isEqualTo(cpoTestInstance.getCountryCode());
		Assertions.assertThat(registeredCPOView.getPartyId()).isEqualTo(cpoTestInstance.getPartyId());
		Assertions.assertThat(registeredCPOView.getName()).isEqualTo(cpoTestInstance.getPartyId());
		Assertions.assertThat(registeredCPOView.getVersionUrl())
				.isEqualTo(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/versions");
		Assertions.assertThat(registeredCPOView.getOutgoingToken()).isEqualTo("token");
		Assertions.assertThat(registeredCPOView.getIncomingToken()).isNull();

		LOG.info("Registering EMSP into CPO...");
		EMSPRegistrationForm emspRegistrationForm = new EMSPRegistrationForm();
		emspRegistrationForm.setCountryCode("FR");
		emspRegistrationForm.setPartyId(emspTestInstance.getPartyId());
		String emspKey = "emsp_FR" + emspTestInstance.getPartyId();
		emspRegistrationForm.setKey(emspKey);
		emspRegistrationForm.setName("name");
		emspRegistrationForm.setVersionUrl(emspTestInstance.getInternalUrl() + "/ocpi/emsp/version");
		emspRegistrationForm.setIncomingToken("token");
		restTemplate.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp", emspRegistrationForm,
				RegisteredEMSPView.class).getBody();

		LOG.info("Triggering handshake from EMSP...");
		RegisteredCPOV211View registeredCPOV211View = restTemplate
				.postForEntity(emspTestInstance.getExternalUrl() + "/api/admin/cpo/" + cpoKey + "/handshake", null,
						RegisteredCPOV211View.class)
				.getBody();

		LOG.info("Retrieving registered EMSP from CPO to be able to check tokens...");
		RegisteredEMSPV211View registeredEMSPV211View = restTemplate
				.getForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp/" + emspKey,
						RegisteredEMSPV211View.class)
				.getBody();

		LOG.info("Checking registered CPO...");
		Assertions.assertThat(registeredCPOV211View.getCredentialsUrl())
				.isEqualTo(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/2.1.1/credentials");
		Assertions.assertThat(registeredCPOV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getIncomingToken())
				.isEqualTo(registeredEMSPV211View.getOutgoingToken());
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEqualTo("token");
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken())
				.isEqualTo(registeredEMSPV211View.getIncomingToken());

		LOG.debug("Retrieving CPO to check it again...");
		registeredCPOV211View = restTemplate.getForEntity(
				emspTestInstance.getExternalUrl() + "/api/admin/cpo/" + cpoKey, RegisteredCPOV211View.class).getBody();

		LOG.info("Checking again CPO...");
		Assertions.assertThat(registeredCPOV211View.getCredentialsUrl())
				.isEqualTo(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/2.1.1/credentials");
		Assertions.assertThat(registeredCPOV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getIncomingToken())
				.isEqualTo(registeredEMSPV211View.getOutgoingToken());
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEqualTo("token");
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken())
				.isEqualTo(registeredEMSPV211View.getIncomingToken());

	}

}
