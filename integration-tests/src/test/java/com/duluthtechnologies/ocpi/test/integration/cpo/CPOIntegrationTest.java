package com.duluthtechnologies.ocpi.test.integration.cpo;

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
import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.EMSPTestInstance;

class CPOIntegrationTest extends AbstractCPOTest {

	private static final Logger LOG = LoggerFactory.getLogger(CPOIntegrationTest.class);

	private RestTemplate restTemplate = new RestTemplate();

	private EMSPTestInstance emspTestInstance;

	@BeforeEach
	void initialize() {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		emspTestInstance = ocpiContainerProvider.createEMSPContainer(network, "FR", partyId);
	}

	@AfterEach
	void teardown() {
		emspTestInstance.teardown();
	}

	@Test
	void testPerformHandshake() {
		LOG.info("Registering EMSP into CPO...");
		String emspKey = "emsp_" + emspTestInstance.getCountryCode() + emspTestInstance.getPartyId();
		EMSPRegistrationForm emspRegistrationForm = new EMSPRegistrationForm();
		emspRegistrationForm.setCountryCode(emspTestInstance.getCountryCode());
		emspRegistrationForm.setPartyId(emspTestInstance.getPartyId());
		emspRegistrationForm.setKey(emspKey);
		emspRegistrationForm.setName(emspTestInstance.getPartyId());
		emspRegistrationForm.setVersionUrl(emspTestInstance.getInternalUrl() + "/ocpi/emsp/versions");
		String token = RandomStringUtils.random(8, true, false);
		emspRegistrationForm.setOutgoingToken(token);
		RegisteredEMSPView registeredEMSPView = restTemplate
				.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp", emspRegistrationForm,
						RegisteredEMSPView.class)
				.getBody();

		LOG.info("Checking registered EMSP...");
		Assertions.assertThat(registeredEMSPView.getKey()).isEqualTo(emspKey);
		Assertions.assertThat(registeredEMSPView.getCountryCode()).isEqualTo(emspTestInstance.getCountryCode());
		Assertions.assertThat(registeredEMSPView.getPartyId()).isEqualTo(emspTestInstance.getPartyId());
		Assertions.assertThat(registeredEMSPView.getName()).isEqualTo(emspTestInstance.getPartyId());
		Assertions.assertThat(registeredEMSPView.getVersionUrl())
				.isEqualTo(emspTestInstance.getInternalUrl() + "/ocpi/emsp/versions");
		Assertions.assertThat(registeredEMSPView.getOutgoingToken()).isEqualTo(token);
		Assertions.assertThat(registeredEMSPView.getIncomingToken()).isNull();

		LOG.info("Registering CPO into EMSP...");
		CPORegistrationForm cpoRegistrationForm = new CPORegistrationForm();
		cpoRegistrationForm.setCountryCode("FR");
		cpoRegistrationForm.setPartyId(cpoTestInstance.getPartyId());
		String cpoKey = "cpo_FR" + cpoTestInstance.getPartyId();
		cpoRegistrationForm.setKey(cpoKey);
		cpoRegistrationForm.setName("name");
		cpoRegistrationForm.setVersionUrl(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/version");
		cpoRegistrationForm.setIncomingToken(token);
		restTemplate.postForEntity(emspTestInstance.getExternalUrl() + "/api/admin/cpo", cpoRegistrationForm,
				RegisteredCPOView.class).getBody();

		LOG.info("Triggering handshake from CPO...");
		RegisteredEMSPV211View registeredEMSPV211View = restTemplate
				.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp/" + emspKey + "/handshake", null,
						RegisteredEMSPV211View.class)
				.getBody();

		LOG.info("Retrieving registered CPO from EMSP to be able to check tokens...");
		RegisteredCPOV211View registeredCPOV211View = restTemplate.getForEntity(
				emspTestInstance.getExternalUrl() + "/api/admin/cpo/" + cpoKey, RegisteredCPOV211View.class).getBody();

		LOG.info("Checking registered EMSP...");
		Assertions.assertThat(registeredEMSPV211View.getCredentialsUrl())
				.isEqualTo(emspTestInstance.getInternalUrl() + "/ocpi/emsp/2.1.1/credentials");
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken())
				.isEqualTo(registeredCPOV211View.getOutgoingToken());
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEqualTo(token);
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken())
				.isEqualTo(registeredCPOV211View.getIncomingToken());

		LOG.debug("Retrieving EMSP to check it again...");
		registeredEMSPV211View = restTemplate
				.getForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp/" + emspKey,
						RegisteredEMSPV211View.class)
				.getBody();

		LOG.info("Checking again EMSP...");
		Assertions.assertThat(registeredEMSPV211View.getCredentialsUrl())
				.isEqualTo(emspTestInstance.getInternalUrl() + "/ocpi/emsp/2.1.1/credentials");
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken())
				.isEqualTo(registeredCPOV211View.getOutgoingToken());
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEqualTo(token);
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken())
				.isEqualTo(registeredCPOV211View.getIncomingToken());

	}

}
