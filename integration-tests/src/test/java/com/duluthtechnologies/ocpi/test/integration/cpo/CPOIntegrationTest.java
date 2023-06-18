package com.duluthtechnologies.ocpi.test.integration.cpo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.Instant;
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
import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.EMSPTestInstance;

class CPOIntegrationTest extends AbstractCPOTest {

	private static final Logger LOG = LoggerFactory.getLogger(CPOIntegrationTest.class);

	private RestTemplate restTemplate = new RestTemplate();

	private EMSPTestInstance emspTestInstance;

	@BeforeEach
	void initialize() {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Instantiating new Emsp test instance with party id [{}] in test class [{}]...", partyId, this);
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
		Assertions.assertThat(registeredEMSPV211View.getLocationsUrl())
				.isEqualTo(emspTestInstance.getInternalUrl() + "/ocpi/emsp/2.1.1/locations");
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
		Assertions.assertThat(registeredEMSPV211View.getLocationsUrl())
				.isEqualTo(emspTestInstance.getInternalUrl() + "/ocpi/emsp/2.1.1/locations");
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken())
				.isEqualTo(registeredCPOV211View.getOutgoingToken());
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEqualTo(token);
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken())
				.isEqualTo(registeredCPOV211View.getIncomingToken());

	}

	@Test
	void testCreateLocation() throws InterruptedException {
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
		restTemplate.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp", emspRegistrationForm,
				RegisteredEMSPView.class).getBody();

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
		restTemplate.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp/" + emspKey + "/handshake", null,
				RegisteredEMSPV211View.class).getBody();

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

		LOG.info("Checking Location returned by CPO...");
		assertThat(locationView).isNotNull();
		assertThat(locationView.getKey()).isNotEmpty();
		assertThat(locationView.getAddress()).isEqualTo(address);
		assertThat(locationView.getCity()).isEqualTo(city);
		assertThat(locationView.getCountryCode()).isEqualTo("FRA");
		assertThat(locationView.getName()).isEqualTo(name);
		assertThat(locationView.getZipCode()).isEqualTo(zipCode);
		assertThat(locationView.getLatitude()).isEqualTo(latitude);
		assertThat(locationView.getLongitude()).isEqualTo(longitude);

		EvseView evseView = locationView.getEvses().get(0);
		assertThat(evseView).isNotNull();
		assertThat(evseView.getKey()).isNotEmpty();
		assertThat(evseView.getEvseId()).isEqualTo(evseId);
		assertThat(evseView.getOcpiId()).isEqualTo(evseOcpiId);

		ConnectorView connectorView = evseView.getConnectors().get(0);
		assertThat(connectorView).isNotNull();
		assertThat(connectorView.getKey()).isNotEmpty();
		String connectorKeyOnCpo = connectorView.getKey();
		assertThat(connectorView.getConnectorId()).isEqualTo("1");
		assertThat(connectorView.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		LOG.info("Waiting a bit to make sure it has been propagated to the EMSP...");
		TimeUnit.SECONDS.sleep(1);

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

		evseView = cpoLocationView.getEvses().get(0);
		assertThat(evseView.getEvseId()).isEqualTo(evseId);
		assertThat(evseView.getOcpiId()).isEqualTo(evseOcpiId);

		connectorView = evseView.getConnectors().get(0);
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
		Instant lastModifiedDateConnector = connectorView.getLastModifiedDate();

		LOG.info("Retrieving CPO connector on EMSP without refresh...");
		connectorView = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/connector/" + connectorView.getKey(),
						ConnectorView.class)
				.getBody();
		assertThat(connectorView).isNotNull();
		assertThat(connectorView.getKey()).isNotNull();
		String connectorKeyOnEmsp = connectorView.getKey();
		assertThat(connectorView.getConnectorId()).isEqualTo("1");
		assertThat(connectorView.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView.getStatus()).isEqualTo(Connector.Status.AVAILABLE);
		// No refresh was requested so the last modified date should not have been
		// changed
		assertThat(connectorView.getLastModifiedDate()).isEqualTo(lastModifiedDateConnector);

		LOG.info("Retrieving CPO connector on EMSP with refresh...");
		connectorView = restTemplate.getForEntity(
				emspTestInstance.getExternalUrl() + "/api/ops/connector/" + connectorView.getKey() + "?refresh=true",
				ConnectorView.class).getBody();
		assertThat(connectorView).isNotNull();
		assertThat(connectorView.getKey()).isNotNull();
		assertThat(connectorView.getConnectorId()).isEqualTo("1");
		assertThat(connectorView.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView.getStatus()).isEqualTo(Connector.Status.AVAILABLE);
		// Refresh was requested so the last modified date should be more recent
		assertThat(connectorView.getLastModifiedDate()).isAfter(lastModifiedDateConnector);

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

		LOG.info("Updating Connector status on CPO...");
		restTemplate.postForEntity(
				cpoTestInstance.getExternalUrl() + "/api/ops/connector/" + connectorKeyOnCpo + "/status/UNAVAILABLE",
				null, Void.class);

		TimeUnit.SECONDS.sleep(1);

		LOG.info("Retrieving CPO connector on EMSP without refresh...");
		connectorView = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/connector/" + connectorKeyOnEmsp,
						ConnectorView.class)
				.getBody();
		assertThat(connectorView).isNotNull();
		assertThat(connectorView.getKey()).isNotNull();
		assertThat(connectorView.getConnectorId()).isEqualTo("1");
		assertThat(connectorView.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView.getStatus()).isEqualTo(Connector.Status.UNAVAILABLE);

	}

}
