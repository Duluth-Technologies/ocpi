package com.duluthtechnologies.ocpi.test.integration.cpo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.api.dto.CPOLocationView;
import com.duluthtechnologies.ocpi.api.dto.CPORegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.ChargingSessionView;
import com.duluthtechnologies.ocpi.api.dto.ConnectorCreationForm;
import com.duluthtechnologies.ocpi.api.dto.ConnectorView;
import com.duluthtechnologies.ocpi.api.dto.EMSPRegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.EvseCreationForm;
import com.duluthtechnologies.ocpi.api.dto.EvseView;
import com.duluthtechnologies.ocpi.api.dto.LocationCreationForm;
import com.duluthtechnologies.ocpi.api.dto.LocationView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredCPOView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPChargingSessionForm;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPChargingSessionView;
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

	@AfterEach
	void teardown() {
		emspTestInstance.teardown();
	}

	@Test
	void testPerformHandshake() {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Instantiating new Emsp test instance with party id [{}] in test class [{}]...", partyId, this);
		emspTestInstance = ocpiContainerProvider.createEMSPContainer(network, "FR", partyId);

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
		Assertions.assertThat(registeredEMSPV211View.getSessionsUrl())
				.isEqualTo(emspTestInstance.getInternalUrl() + "/ocpi/emsp/2.1.1/sessions");
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
		Assertions.assertThat(registeredEMSPV211View.getSessionsUrl())
				.isEqualTo(emspTestInstance.getInternalUrl() + "/ocpi/emsp/2.1.1/sessions");
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getIncomingToken())
				.isEqualTo(registeredCPOV211View.getOutgoingToken());
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEmpty();
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEqualTo(token);
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken())
				.isEqualTo(registeredCPOV211View.getIncomingToken());

	}

	@Test
	void testPerformHandshakeTwice() {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Instantiating new Emsp test instance with party id [{}] in test class [{}]...", partyId, this);
		emspTestInstance = ocpiContainerProvider.createEMSPContainer(network, "FR", partyId);

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

		// Store the token to make sure they are modified after second handshake
		String incomingToken = registeredEMSPV211View.getIncomingToken();
		String outgoingToken = registeredEMSPV211View.getOutgoingToken();

		LOG.info("Triggering again handshake from CPO...");
		registeredEMSPV211View = restTemplate
				.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/emsp/" + emspKey + "/handshake", null,
						RegisteredEMSPV211View.class)
				.getBody();

		Assertions.assertThat(registeredEMSPV211View.getIncomingToken()).isNotEqualTo(incomingToken);
		Assertions.assertThat(registeredEMSPV211View.getOutgoingToken()).isNotEqualTo(outgoingToken);

	}

	@Test
	void testCreateLocation() throws InterruptedException {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Instantiating new Emsp test instance with party id [{}] in test class [{}]...", partyId, this);
		emspTestInstance = ocpiContainerProvider.createEMSPContainer(network, "FR", partyId);

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

	@Test
	void testCreateAndUpdateSession() throws InterruptedException {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Instantiating new Emsp test instance with party id [{}] in test class [{}]...", partyId, this);
		emspTestInstance = ocpiContainerProvider.createEMSPContainer(network, "FR", partyId);

		LOG.info("Creating Location on CPO...");
		// First connector for first Evse
		ConnectorCreationForm connectorCreationForm1 = new ConnectorCreationForm();
		connectorCreationForm1.setConnectorId("1");
		connectorCreationForm1.setFormat(Format.CABLE);
		connectorCreationForm1.setType(Type.IEC_62196_T2_COMBO);
		connectorCreationForm1.setMaximumAmperage(100);
		connectorCreationForm1.setPowerType(PowerType.DC);
		connectorCreationForm1.setMaximumVoltage(1000);
		connectorCreationForm1.setStatus(Connector.Status.AVAILABLE);

		// First Evse
		EvseCreationForm evseCreationForm1 = new EvseCreationForm();
		String evseId1 = UUID.randomUUID().toString();
		String evseOcpiId1 = UUID.randomUUID().toString();
		evseCreationForm1.setEvseId(evseId1);
		evseCreationForm1.setOcpiId(evseOcpiId1);
		evseCreationForm1.setConnectors(List.of(connectorCreationForm1));

		// Second connector for second Evse
		ConnectorCreationForm connectorCreationForm2 = new ConnectorCreationForm();
		connectorCreationForm2.setConnectorId("1");
		connectorCreationForm2.setFormat(Format.CABLE);
		connectorCreationForm2.setType(Type.IEC_62196_T2_COMBO);
		connectorCreationForm2.setMaximumAmperage(100);
		connectorCreationForm2.setPowerType(PowerType.DC);
		connectorCreationForm2.setMaximumVoltage(1000);
		connectorCreationForm2.setStatus(Connector.Status.AVAILABLE);

		// Second Evse
		EvseCreationForm evseCreationForm2 = new EvseCreationForm();
		String evseId2 = UUID.randomUUID().toString();
		String evseOcpiId2 = UUID.randomUUID().toString();
		evseCreationForm2.setEvseId(evseId2);
		evseCreationForm2.setOcpiId(evseOcpiId2);
		evseCreationForm2.setConnectors(List.of(connectorCreationForm2));

		// Location
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
		locationCreationForm.setEvses(List.of(evseCreationForm1, evseCreationForm2)); // Here the Location is linked to
																						// two Evses
		LocationView locationView = restTemplate.postForEntity(cpoTestInstance.getExternalUrl() + "/api/admin/location",
				locationCreationForm, LocationView.class).getBody();

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

		LOG.info("Waiting a bit so that EMSP has synchronized with CPO...");
		TimeUnit.SECONDS.sleep(30);

		LOG.info("Updating Connector status on CPO...");
		String connectorKeyOnCpo = locationView.getEvses().get(0).getConnectors().get(0).getKey();
		restTemplate.postForEntity(
				cpoTestInstance.getExternalUrl() + "/api/ops/connector/" + connectorKeyOnCpo + "/status/UNAVAILABLE",
				null, Void.class);

		Instant createSessionInstant = Instant.now();

		LOG.info("Creating Session on CPO...");
		RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm = new RegisteredEMSPChargingSessionForm();
		registeredEMSPChargingSessionForm.setConnectorKey(connectorKeyOnCpo);
		String chargingSessionOcpiId = UUID.randomUUID().toString();
		registeredEMSPChargingSessionForm.setOcpiId(chargingSessionOcpiId);
		registeredEMSPChargingSessionForm.setRegisteredEmspKey(registeredEMSPView.getKey());
		RegisteredEMSPChargingSessionView registeredEMSPChargingSessionView = restTemplate
				.postForEntity(cpoTestInstance.getExternalUrl() + "/api/ops/charging-session",
						registeredEMSPChargingSessionForm, RegisteredEMSPChargingSessionView.class)
				.getBody();

		LOG.info("Verifying created Session on CPO...");
		assertThat(registeredEMSPChargingSessionView).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getKey()).isNotEmpty();
		assertThat(registeredEMSPChargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(registeredEMSPChargingSessionView.getCreatedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getStartDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getStopDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getDisconnectDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getConnectorKey())
				.isEqualTo(locationView.getEvses().get(0).getConnectors().get(0).getKey());
		assertThat(registeredEMSPChargingSessionView.getEnergyDeliveredInWh()).isNull();
		assertThat(registeredEMSPChargingSessionView.getLastModifiedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNull();
		assertThat(registeredEMSPChargingSessionView.getRegisteredEmspKey()).isEqualTo(registeredEMSPView.getKey());

		LOG.info("Retrieving CPO location on EMSP...");
		CPOLocationView[] cpoLocationViews = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/cpo/" + cpoKey + "/locations",
						CPOLocationView[].class)
				.getBody();
		CPOLocationView cpoLocationView = Stream.of(cpoLocationViews)
				.filter(l -> l.getOcpiId().equals(locationView.getOcpiId())).findFirst().get();
		ConnectorView connectorView = cpoLocationView.getEvses().stream().flatMap(evse -> evse.getConnectors().stream())
				.filter(c -> c.getConnectorId().equals("1")).findFirst().get();
		String connectorKeyOnEmsp = connectorView.getKey();

		LOG.info("Retrieving ChargingSession on EMSP...");
		ChargingSessionView[] chargingSessionViews = restTemplate.getForEntity(
				emspTestInstance.getExternalUrl()
						+ "/api/ops/charging-session?dateFrom={dateFrom}&dateTo={dateTo}&connectorKey={connectorKey}",
				ChargingSessionView[].class, createSessionInstant, Instant.now(), connectorKeyOnEmsp).getBody();

		assertThat(chargingSessionViews).hasSize(1);
		ChargingSessionView chargingSessionView = chargingSessionViews[0];
		assertThat(chargingSessionView).isNotNull();
		assertThat(chargingSessionView.getKey()).isNotEmpty();
		assertThat(chargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(chargingSessionView.getCreatedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getStartDate()).isNull();
		assertThat(chargingSessionView.getStopDate()).isNull();
		assertThat(chargingSessionView.getDisconnectDate()).isNull();
		assertThat(chargingSessionView.getConnectorKey()).isEqualTo(connectorKeyOnEmsp);
		assertThat(chargingSessionView.getEnergyDeliveredInWh()).isNull();
		assertThat(chargingSessionView.getLastModifiedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getCost()).isNull();

		LOG.info("Updating Session on CPO with start date...");
		registeredEMSPChargingSessionForm.setKey(registeredEMSPChargingSessionView.getKey());
		Instant startDate = Instant.now();
		registeredEMSPChargingSessionForm.setStartDate(startDate);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RegisteredEMSPChargingSessionForm> requestEntity = new HttpEntity<>(
				registeredEMSPChargingSessionForm, headers);
		registeredEMSPChargingSessionView = restTemplate
				.exchange(cpoTestInstance.getExternalUrl() + "/api/ops/charging-session", HttpMethod.PUT, requestEntity,
						RegisteredEMSPChargingSessionView.class)
				.getBody();

		LOG.info("Verifying updated Session on CPO...");
		assertThat(registeredEMSPChargingSessionView).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getKey()).isNotEmpty();
		assertThat(registeredEMSPChargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(registeredEMSPChargingSessionView.getCreatedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getStopDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getDisconnectDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getConnectorKey())
				.isEqualTo(locationView.getEvses().get(0).getConnectors().get(0).getKey());
		assertThat(registeredEMSPChargingSessionView.getEnergyDeliveredInWh()).isNull();
		assertThat(registeredEMSPChargingSessionView.getLastModifiedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNull();
		assertThat(registeredEMSPChargingSessionView.getRegisteredEmspKey()).isEqualTo(registeredEMSPView.getKey());

		LOG.info("Retrieving ChargingSession on CPO after update...");
		registeredEMSPChargingSessionView = restTemplate
				.getForEntity(cpoTestInstance.getExternalUrl() + "/api/ops/charging-session/{key}",
						RegisteredEMSPChargingSessionView.class, registeredEMSPChargingSessionView.getKey())
				.getBody();

		LOG.info("Verifying retrieved updated Session on CPO...");
		assertThat(registeredEMSPChargingSessionView).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getKey()).isNotEmpty();
		assertThat(registeredEMSPChargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(registeredEMSPChargingSessionView.getCreatedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getStopDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getDisconnectDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getConnectorKey())
				.isEqualTo(locationView.getEvses().get(0).getConnectors().get(0).getKey());
		assertThat(registeredEMSPChargingSessionView.getEnergyDeliveredInWh()).isNull();
		assertThat(registeredEMSPChargingSessionView.getLastModifiedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNull();
		assertThat(registeredEMSPChargingSessionView.getRegisteredEmspKey()).isEqualTo(registeredEMSPView.getKey());

		LOG.info("Retrieving ChargingSession on EMSP after update...");
		chargingSessionView = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/charging-session/{key}",
						ChargingSessionView.class, chargingSessionView.getKey())
				.getBody();

		// Verify ChargingSession
		assertThat(chargingSessionView).isNotNull();
		assertThat(chargingSessionView.getKey()).isNotEmpty();
		assertThat(chargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(chargingSessionView.getCreatedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getStopDate()).isNull();
		assertThat(chargingSessionView.getDisconnectDate()).isNull();
		assertThat(chargingSessionView.getConnectorKey()).isEqualTo(connectorKeyOnEmsp);
		assertThat(chargingSessionView.getEnergyDeliveredInWh()).isNull();
		assertThat(chargingSessionView.getLastModifiedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getCost()).isNull();

		LOG.info("Updating Session on CPO with cost and energy delivered...");
		registeredEMSPChargingSessionForm.setEnergyDeliveredInWh(1);
		RegisteredEMSPChargingSessionForm.Cost cost = new RegisteredEMSPChargingSessionForm.Cost();
		cost.setCurrency(Currency.getInstance("EUR"));
		cost.setFractionalAmount(33);
		registeredEMSPChargingSessionForm.setCost(cost);
		registeredEMSPChargingSessionView = restTemplate
				.exchange(cpoTestInstance.getExternalUrl() + "/api/ops/charging-session", HttpMethod.PUT, requestEntity,
						RegisteredEMSPChargingSessionView.class)
				.getBody();

		LOG.info("Verifying updated Session on CPO...");
		assertThat(registeredEMSPChargingSessionView).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getKey()).isNotEmpty();
		assertThat(registeredEMSPChargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(registeredEMSPChargingSessionView.getCreatedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getStopDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getDisconnectDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getConnectorKey())
				.isEqualTo(locationView.getEvses().get(0).getConnectors().get(0).getKey());
		assertThat(registeredEMSPChargingSessionView.getEnergyDeliveredInWh()).isEqualTo(1);
		assertThat(registeredEMSPChargingSessionView.getLastModifiedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(33);
		assertThat(registeredEMSPChargingSessionView.getRegisteredEmspKey()).isEqualTo(registeredEMSPView.getKey());

		LOG.info("Retrieving ChargingSession on EMSP after update...");
		chargingSessionView = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/charging-session/{key}",
						ChargingSessionView.class, chargingSessionView.getKey())
				.getBody();

		// Verify ChargingSession
		assertThat(chargingSessionView).isNotNull();
		assertThat(chargingSessionView.getKey()).isNotEmpty();
		assertThat(chargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(chargingSessionView.getCreatedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getStopDate()).isNull();
		assertThat(chargingSessionView.getDisconnectDate()).isNull();
		assertThat(chargingSessionView.getConnectorKey()).isEqualTo(connectorKeyOnEmsp);
		assertThat(chargingSessionView.getEnergyDeliveredInWh()).isEqualTo(1);
		assertThat(chargingSessionView.getLastModifiedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(33);

		LOG.info("Updating Session on CPO with cost and energy delivered...");
		registeredEMSPChargingSessionForm.setEnergyDeliveredInWh(2);
		cost = new RegisteredEMSPChargingSessionForm.Cost();
		cost.setCurrency(Currency.getInstance("EUR"));
		cost.setFractionalAmount(66);
		registeredEMSPChargingSessionForm.setCost(cost);
		registeredEMSPChargingSessionView = restTemplate
				.exchange(cpoTestInstance.getExternalUrl() + "/api/ops/charging-session", HttpMethod.PUT, requestEntity,
						RegisteredEMSPChargingSessionView.class)
				.getBody();

		LOG.info("Verifying updated Session on CPO...");
		assertThat(registeredEMSPChargingSessionView).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getKey()).isNotEmpty();
		assertThat(registeredEMSPChargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(registeredEMSPChargingSessionView.getCreatedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getStopDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getDisconnectDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getConnectorKey())
				.isEqualTo(locationView.getEvses().get(0).getConnectors().get(0).getKey());
		assertThat(registeredEMSPChargingSessionView.getEnergyDeliveredInWh()).isEqualTo(2);
		assertThat(registeredEMSPChargingSessionView.getLastModifiedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(66);
		assertThat(registeredEMSPChargingSessionView.getRegisteredEmspKey()).isEqualTo(registeredEMSPView.getKey());

		LOG.info("Retrieving ChargingSession on EMSP after update...");
		chargingSessionView = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/charging-session/{key}",
						ChargingSessionView.class, chargingSessionView.getKey())
				.getBody();

		// Verify ChargingSession
		assertThat(chargingSessionView).isNotNull();
		assertThat(chargingSessionView.getKey()).isNotEmpty();
		assertThat(chargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(chargingSessionView.getCreatedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getStopDate()).isNull();
		assertThat(chargingSessionView.getDisconnectDate()).isNull();
		assertThat(chargingSessionView.getConnectorKey()).isEqualTo(connectorKeyOnEmsp);
		assertThat(chargingSessionView.getEnergyDeliveredInWh()).isEqualTo(2);
		assertThat(chargingSessionView.getLastModifiedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(66);

		LOG.info("Updating Session on CPO with cost and energy delivered and stop date...");
		registeredEMSPChargingSessionForm.setEnergyDeliveredInWh(3);
		cost = new RegisteredEMSPChargingSessionForm.Cost();
		cost.setCurrency(Currency.getInstance("EUR"));
		cost.setFractionalAmount(100);
		registeredEMSPChargingSessionForm.setCost(cost);
		Instant stopDate = Instant.now();
		registeredEMSPChargingSessionForm.setStopDate(stopDate);
		registeredEMSPChargingSessionView = restTemplate
				.exchange(cpoTestInstance.getExternalUrl() + "/api/ops/charging-session", HttpMethod.PUT, requestEntity,
						RegisteredEMSPChargingSessionView.class)
				.getBody();

		LOG.info("Verifying updated Session on CPO...");
		assertThat(registeredEMSPChargingSessionView).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getKey()).isNotEmpty();
		assertThat(registeredEMSPChargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(registeredEMSPChargingSessionView.getCreatedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getStopDate()).isCloseTo(stopDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getDisconnectDate()).isNull();
		assertThat(registeredEMSPChargingSessionView.getConnectorKey())
				.isEqualTo(locationView.getEvses().get(0).getConnectors().get(0).getKey());
		assertThat(registeredEMSPChargingSessionView.getEnergyDeliveredInWh()).isEqualTo(3);
		assertThat(registeredEMSPChargingSessionView.getLastModifiedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(100);
		assertThat(registeredEMSPChargingSessionView.getRegisteredEmspKey()).isEqualTo(registeredEMSPView.getKey());

		LOG.info("Retrieving ChargingSession on EMSP after update...");
		chargingSessionView = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/charging-session/{key}",
						ChargingSessionView.class, chargingSessionView.getKey())
				.getBody();

		// Verify ChargingSession
		assertThat(chargingSessionView).isNotNull();
		assertThat(chargingSessionView.getKey()).isNotEmpty();
		assertThat(chargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(chargingSessionView.getCreatedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getStopDate()).isCloseTo(stopDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getDisconnectDate()).isNull();
		assertThat(chargingSessionView.getConnectorKey()).isEqualTo(connectorKeyOnEmsp);
		assertThat(chargingSessionView.getEnergyDeliveredInWh()).isEqualTo(3);
		assertThat(chargingSessionView.getLastModifiedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(100);

		LOG.info("Updating Session on CPO with disconnect date...");
		cost = new RegisteredEMSPChargingSessionForm.Cost();
		cost.setCurrency(Currency.getInstance("EUR"));
		cost.setFractionalAmount(120);
		registeredEMSPChargingSessionForm.setCost(cost);
		Instant disconnectDate = Instant.now();
		registeredEMSPChargingSessionForm.setDisconnectDate(disconnectDate);
		registeredEMSPChargingSessionView = restTemplate
				.exchange(cpoTestInstance.getExternalUrl() + "/api/ops/charging-session", HttpMethod.PUT, requestEntity,
						RegisteredEMSPChargingSessionView.class)
				.getBody();

		LOG.info("Verifying updated Session on CPO...");
		assertThat(registeredEMSPChargingSessionView).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getKey()).isNotEmpty();
		assertThat(registeredEMSPChargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(registeredEMSPChargingSessionView.getCreatedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getStopDate()).isCloseTo(stopDate, within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getDisconnectDate()).isCloseTo(disconnectDate,
				within(1, ChronoUnit.MILLIS));
		assertThat(registeredEMSPChargingSessionView.getConnectorKey())
				.isEqualTo(locationView.getEvses().get(0).getConnectors().get(0).getKey());
		assertThat(registeredEMSPChargingSessionView.getEnergyDeliveredInWh()).isEqualTo(3);
		assertThat(registeredEMSPChargingSessionView.getLastModifiedDate()).isNotNull()
				.isAfterOrEqualTo(createSessionInstant).isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(120);
		assertThat(registeredEMSPChargingSessionView.getRegisteredEmspKey()).isEqualTo(registeredEMSPView.getKey());

		LOG.info("Retrieving ChargingSession on EMSP after update...");
		chargingSessionView = restTemplate
				.getForEntity(emspTestInstance.getExternalUrl() + "/api/ops/charging-session/{key}",
						ChargingSessionView.class, chargingSessionView.getKey())
				.getBody();

		// Verify ChargingSession
		assertThat(chargingSessionView).isNotNull();
		assertThat(chargingSessionView.getKey()).isNotEmpty();
		assertThat(chargingSessionView.getOcpiId()).isEqualTo(chargingSessionOcpiId);
		assertThat(chargingSessionView.getCreatedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(chargingSessionView.getStartDate()).isCloseTo(startDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getStopDate()).isCloseTo(stopDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getDisconnectDate()).isCloseTo(disconnectDate, within(1, ChronoUnit.SECONDS));
		assertThat(chargingSessionView.getConnectorKey()).isEqualTo(connectorKeyOnEmsp);
		assertThat(chargingSessionView.getEnergyDeliveredInWh()).isEqualTo(3);
		assertThat(chargingSessionView.getLastModifiedDate()).isNotNull().isAfterOrEqualTo(createSessionInstant)
				.isBeforeOrEqualTo(Instant.now());
		assertThat(registeredEMSPChargingSessionView.getCost()).isNotNull();
		assertThat(registeredEMSPChargingSessionView.getCost().getCurrency()).isEqualTo(Currency.getInstance("EUR"));
		assertThat(registeredEMSPChargingSessionView.getCost().getFractionalAmount()).isEqualTo(120);
	}

}
