package com.duluthtechnologies.ocpi.test.integration.emsp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.api.dto.CPOLocationView;
import com.duluthtechnologies.ocpi.api.dto.CPORegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.ConnectorCreationForm;
import com.duluthtechnologies.ocpi.api.dto.ConnectorView;
import com.duluthtechnologies.ocpi.api.dto.EMSPRegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.EvseCreationForm;
import com.duluthtechnologies.ocpi.api.dto.EvseView;
import com.duluthtechnologies.ocpi.api.dto.LocationCreationForm;
import com.duluthtechnologies.ocpi.api.dto.LocationView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredCPOView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPView;
import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredCPOV211View;
import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredEMSPV211View;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Format;
import com.duluthtechnologies.ocpi.core.model.Connector.PowerType;
import com.duluthtechnologies.ocpi.core.model.Connector.Type;
import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.CPOTestInstance;

class EMSPIntegrationTest extends AbstractEMSPTest {

	private static final Logger LOG = LoggerFactory.getLogger(EMSPIntegrationTest.class);

	private RestTemplate restTemplate = new RestTemplate();

	private CPOTestInstance cpoTestInstance;	

	@AfterEach
	void teardown() {
		cpoTestInstance.teardown();
	}

	@Test
	void testPerformHandshake() {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Creating test CPO container with party id [{}]...", partyId);
		cpoTestInstance = ocpiContainerProvider.createCPOContainer(network, "FR", partyId, true);
		
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
		Assertions.assertThat(registeredCPOV211View.getLocationsUrl())
				.isEqualTo(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/2.1.1/locations");
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
		Assertions.assertThat(registeredCPOV211View.getLocationsUrl())
				.isEqualTo(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/2.1.1/locations");
		Assertions.assertThat(registeredCPOV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getIncomingToken())
				.isEqualTo(registeredEMSPV211View.getOutgoingToken());
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEqualTo("token");
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken())
				.isEqualTo(registeredEMSPV211View.getIncomingToken());

	}
	
	@Test
	void testPerformHandshakeCPONoZInTimestampSerialization() {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Creating test CPO container with party id [{}]...", partyId);
		cpoTestInstance = ocpiContainerProvider.createCPOContainer(network, "FR", partyId, false);
		
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
		Assertions.assertThat(registeredCPOV211View.getLocationsUrl())
				.isEqualTo(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/2.1.1/locations");
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
		Assertions.assertThat(registeredCPOV211View.getLocationsUrl())
				.isEqualTo(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/2.1.1/locations");
		Assertions.assertThat(registeredCPOV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getIncomingToken())
				.isEqualTo(registeredEMSPV211View.getOutgoingToken());
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken()).isNotEqualTo("token");
		Assertions.assertThat(registeredCPOV211View.getOutgoingToken())
				.isEqualTo(registeredEMSPV211View.getIncomingToken());

	}

	@Test
	void testSynchronizeLocation() throws InterruptedException {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Creating test CPO container with party id [{}]...", partyId);
		cpoTestInstance = ocpiContainerProvider.createCPOContainer(network, "FR", partyId, true);
		
		LOG.info("Creating Location on CPO...");
		ConnectorCreationForm connectorCreationForm = new ConnectorCreationForm();
		connectorCreationForm.setConnectorId("1");
		connectorCreationForm.setFormat(Format.CABLE);
		connectorCreationForm.setType(Type.IEC_62196_T2_COMBO);
		connectorCreationForm.setMaximumAmperage(100);
		connectorCreationForm.setPowerType(PowerType.DC);
		connectorCreationForm.setMaximumVoltage(1000);
		connectorCreationForm.setStatus(Connector.Status.AVAILABLE);

		EvseCreationForm evseCreationForm = new EvseCreationForm();
		String evseId = UUID.randomUUID().toString();
		String evseOcpiId = UUID.randomUUID().toString();
		evseCreationForm.setEvseId(evseId);
		evseCreationForm.setOcpiId(evseOcpiId);
		evseCreationForm.setConnectors(List.of(connectorCreationForm));

		LocationCreationForm locationCreationForm = new LocationCreationForm();
		String address = RandomStringUtils.random(16, true, false);
		String city = RandomStringUtils.random(16, true, false);
		String name = RandomStringUtils.random(16, true, false);
		String zipCode = RandomStringUtils.random(5, false, true);
		double latitude = RandomUtils.nextDouble(40, 50);
		double longitude = RandomUtils.nextDouble(0, 10);
		locationCreationForm.setAddress(address);
		locationCreationForm.setCity(city);
		locationCreationForm.setName(name);
		locationCreationForm.setZipCode(zipCode);
		locationCreationForm.setLatitude(latitude);
		locationCreationForm.setLongitude(longitude);
		locationCreationForm.setCountryCode("FRA");
		locationCreationForm.setEvses(List.of(evseCreationForm));
		LocationView locationView = restTemplate.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/location",
				locationCreationForm, LocationView.class).getBody();

		LOG.info("Registering CPO into EMSP...");
		String cpoKey = "cpo_" + cpoTestInstance.getCountryCode() + cpoTestInstance.getPartyId();
		CPORegistrationForm cpoRegistrationForm = new CPORegistrationForm();
		cpoRegistrationForm.setCountryCode(cpoTestInstance.getCountryCode());
		cpoRegistrationForm.setPartyId(cpoTestInstance.getPartyId());
		cpoRegistrationForm.setKey(cpoKey);
		cpoRegistrationForm.setName(cpoTestInstance.getPartyId());
		cpoRegistrationForm.setVersionUrl(cpoTestInstance.getInternalUrl() + "/ocpi/cpo/versions");
		cpoRegistrationForm.setOutgoingToken("token");
		restTemplate.postForEntity(emspTestInstance.getExternalUrl() + "/api/admin/cpo", cpoRegistrationForm,
				RegisteredCPOView.class).getBody();

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
		restTemplate.postForEntity(emspTestInstance.getExternalUrl() + "/api/admin/cpo/" + cpoKey + "/handshake", null,
				RegisteredCPOV211View.class);

		LOG.info("Waiting a bit so that EMSP has synchronized with CPO...");
		TimeUnit.SECONDS.sleep(30);

		LOG.info("Retrieving CPO location on EMSP...");
		CPOLocationView[] cpoLocationViews = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/cpo/" + cpoKey + "/locations",
						CPOLocationView[].class)
				.getBody();

		LOG.info("Checking CPO location retrieved from EMSP...");
		assertThat(cpoLocationViews).hasSize(1);
		CPOLocationView cpoLocationView = cpoLocationViews[0];
		assertThat(cpoLocationView.getCpoKey()).isEqualTo(cpoKey);
		assertThat(cpoLocationView.getAddress()).isEqualTo(address);
		assertThat(cpoLocationView.getCity()).isEqualTo(city);
		assertThat(cpoLocationView.getCountryCode()).isEqualTo("FRA");
		assertThat(cpoLocationView.getName()).isEqualTo(name);
		assertThat(cpoLocationView.getZipCode()).isEqualTo(zipCode);
		assertThat(cpoLocationView.getLatitude()).isCloseTo(latitude, within(1e-6));
		assertThat(cpoLocationView.getLongitude()).isCloseTo(longitude, within(1e-6));

		EvseView evseView = cpoLocationView.getEvses().get(0);
		assertThat(evseView.getEvseId()).isEqualTo(evseId);
		assertThat(evseView.getOcpiId()).isEqualTo(evseOcpiId);

		ConnectorView connectorView = evseView.getConnectors().get(0);
		assertThat(evseView).isNotNull();
		assertThat(connectorView).isNotNull();
		assertThat(connectorView.getKey()).isNotNull();
		assertThat(connectorView.getConnectorId()).isEqualTo("1");
		assertThat(connectorView.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		LOG.info("Retrieving EMSP location from the CPO...");
		locationView = restTemplate.getForEntity(
				cpoTestInstance.getExternalUrl() + "/api/ops/emsp/" + emspKey + "/location/" + locationView.getKey(),
				LocationView.class).getBody();

		assertThat(locationView).isNotNull();
		assertThat(locationView.getAddress()).isEqualTo(address);
		assertThat(locationView.getCity()).isEqualTo(city);
		assertThat(locationView.getCountryCode()).isEqualTo("FRA");
		assertThat(locationView.getName()).isEqualTo(name);
		assertThat(locationView.getZipCode()).isEqualTo(zipCode);
		assertThat(locationView.getLatitude()).isCloseTo(latitude, within(1e-6));
		assertThat(locationView.getLongitude()).isCloseTo(longitude, within(1e-6));

		evseView = locationView.getEvses().get(0);
		assertThat(evseView).isNotNull();
		assertThat(evseView.getEvseId()).isEqualTo(evseId);
		assertThat(evseView.getOcpiId()).isEqualTo(evseOcpiId);

		connectorView = evseView.getConnectors().get(0);
		assertThat(connectorView).isNotNull();
		assertThat(connectorView.getConnectorId()).isEqualTo("1");
		assertThat(connectorView.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

	}

}
