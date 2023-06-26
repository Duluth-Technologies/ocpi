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
		// First connector for first Evse
		ConnectorCreationForm connectorCreationForm1 = new ConnectorCreationForm();
		connectorCreationForm1.setConnectorId("1");
		connectorCreationForm1.setFormat(Format.CABLE);
		connectorCreationForm1.setType(Type.IEC_62196_T2_COMBO);
		connectorCreationForm1.setMaximumAmperage(100);
		connectorCreationForm1.setPowerType(PowerType.DC);
		connectorCreationForm1.setMaximumVoltage(1000);
		connectorCreationForm1.setStatus(Connector.Status.AVAILABLE);

		// Second connector for first Evse
		ConnectorCreationForm connectorCreationForm2 = new ConnectorCreationForm();
		connectorCreationForm2.setConnectorId("2");
		connectorCreationForm2.setFormat(Format.CABLE);
		connectorCreationForm2.setType(Type.IEC_62196_T2_COMBO);
		connectorCreationForm2.setMaximumAmperage(100);
		connectorCreationForm2.setPowerType(PowerType.DC);
		connectorCreationForm2.setMaximumVoltage(1000);
		connectorCreationForm2.setStatus(Connector.Status.AVAILABLE);

		// First Evse
		EvseCreationForm evseCreationForm1 = new EvseCreationForm();
		String evseId1 = UUID.randomUUID().toString();
		String evseOcpiId1 = UUID.randomUUID().toString();
		evseCreationForm1.setEvseId(evseId1);
		evseCreationForm1.setOcpiId(evseOcpiId1);
		evseCreationForm1.setConnectors(List.of(connectorCreationForm1, connectorCreationForm2));

		// Third connector for second Evse
		ConnectorCreationForm connectorCreationForm3 = new ConnectorCreationForm();
		connectorCreationForm3.setConnectorId("1");
		connectorCreationForm3.setFormat(Format.CABLE);
		connectorCreationForm3.setType(Type.IEC_62196_T2_COMBO);
		connectorCreationForm3.setMaximumAmperage(100);
		connectorCreationForm3.setPowerType(PowerType.DC);
		connectorCreationForm3.setMaximumVoltage(1000);
		connectorCreationForm3.setStatus(Connector.Status.AVAILABLE);

		// Fourth connector for second Evse
		ConnectorCreationForm connectorCreationForm4 = new ConnectorCreationForm();
		connectorCreationForm4.setConnectorId("2");
		connectorCreationForm4.setFormat(Format.CABLE);
		connectorCreationForm4.setType(Type.IEC_62196_T2_COMBO);
		connectorCreationForm4.setMaximumAmperage(100);
		connectorCreationForm4.setPowerType(PowerType.DC);
		connectorCreationForm4.setMaximumVoltage(1000);
		connectorCreationForm4.setStatus(Connector.Status.AVAILABLE);

		// Second Evse
		EvseCreationForm evseCreationForm2 = new EvseCreationForm();
		String evseId2 = UUID.randomUUID().toString();
		String evseOcpiId2 = UUID.randomUUID().toString();
		evseCreationForm2.setEvseId(evseId2);
		evseCreationForm2.setOcpiId(evseOcpiId2);
		evseCreationForm2.setConnectors(List.of(connectorCreationForm3, connectorCreationForm4));

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
		locationCreationForm.setEvses(List.of(evseCreationForm1, evseCreationForm2)); // Here the Location is linked to two Evses
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

		// check that we have two EVSEs
		assertThat(cpoLocationView.getEvses().size()).isEqualTo(2);

		// checking first EVSE
		EvseView evseView1 = cpoLocationView.getEvses().get(0);
		assertThat(evseView1.getEvseId()).isEqualTo(evseId1);
		assertThat(evseView1.getOcpiId()).isEqualTo(evseOcpiId1);

		// checking first EVSE's connectors
		assertThat(evseView1.getConnectors().size()).isEqualTo(2);

		ConnectorView connectorView1 = evseView1.getConnectors().get(0);
		assertThat(connectorView1.getConnectorId()).isEqualTo("1");
		assertThat(connectorView1.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView1.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView1.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView1.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView1.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView1.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		ConnectorView connectorView2 = evseView1.getConnectors().get(1);
		assertThat(connectorView2.getConnectorId()).isEqualTo("2");
		assertThat(connectorView2.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView2.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView2.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView2.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView2.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView2.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		// checking second EVSE
		EvseView evseView2 = cpoLocationView.getEvses().get(1);
		assertThat(evseView2.getEvseId()).isEqualTo(evseId2);
		assertThat(evseView2.getOcpiId()).isEqualTo(evseOcpiId2);

		// checking second EVSE's connectors
		assertThat(evseView2.getConnectors().size()).isEqualTo(2);

		ConnectorView connectorView3 = evseView2.getConnectors().get(0);
		assertThat(connectorView3.getConnectorId()).isEqualTo("1");
		assertThat(connectorView3.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView3.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView3.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView3.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView3.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView3.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		ConnectorView connectorView4 = evseView2.getConnectors().get(1);
		assertThat(connectorView4.getConnectorId()).isEqualTo("2");
		assertThat(connectorView4.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView4.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView4.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView4.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView4.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView4.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		LOG.info("Retrieving EMSP location from the CPO...");
		locationView = restTemplate.getForEntity(
				cpoTestInstance.getExternalUrl() + "/api/ops/emsp/" + emspKey + "/location/" + locationView.getKey(),
				LocationView.class).getBody();

		assertThat(locationView.getAddress()).isEqualTo(address);
		assertThat(locationView.getCity()).isEqualTo(city);
		assertThat(locationView.getCountryCode()).isEqualTo("FRA");
		assertThat(locationView.getName()).isEqualTo(name);
		assertThat(locationView.getZipCode()).isEqualTo(zipCode);
		assertThat(locationView.getLatitude()).isCloseTo(latitude, within(1e-6));
		assertThat(locationView.getLongitude()).isCloseTo(longitude, within(1e-6));

		// check that we have two EVSEs
		assertThat(locationView.getEvses().size()).isEqualTo(2);

		// checking first EVSE
		evseView1 = locationView.getEvses().get(0);
		assertThat(evseView1.getEvseId()).isEqualTo(evseId1);
		assertThat(evseView1.getOcpiId()).isEqualTo(evseOcpiId1);

		// checking first EVSE's connectors
		assertThat(evseView1.getConnectors().size()).isEqualTo(2);

		connectorView1 = evseView1.getConnectors().get(0);
		assertThat(connectorView1.getConnectorId()).isEqualTo("1");
		assertThat(connectorView1.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView1.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView1.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView1.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView1.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView1.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		connectorView2 = evseView1.getConnectors().get(1);
		assertThat(connectorView2.getConnectorId()).isEqualTo("2");
		assertThat(connectorView2.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView2.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView2.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView2.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView2.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView2.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		// checking second EVSE
		evseView2 = locationView.getEvses().get(1);
		assertThat(evseView2.getEvseId()).isEqualTo(evseId2);
		assertThat(evseView2.getOcpiId()).isEqualTo(evseOcpiId2);

		// checking second EVSE's connectors
		assertThat(evseView2.getConnectors().size()).isEqualTo(2);

		connectorView3 = evseView2.getConnectors().get(0);
		assertThat(connectorView3.getConnectorId()).isEqualTo("1");
		assertThat(connectorView3.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView3.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView3.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView3.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView3.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView3.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

		connectorView4 = evseView2.getConnectors().get(1);
		assertThat(connectorView4.getConnectorId()).isEqualTo("2");
		assertThat(connectorView4.getFormat()).isEqualTo(Format.CABLE);
		assertThat(connectorView4.getType()).isEqualTo(Type.IEC_62196_T2_COMBO);
		assertThat(connectorView4.getMaximumAmperage()).isEqualTo(100);
		assertThat(connectorView4.getPowerType()).isEqualTo(PowerType.DC);
		assertThat(connectorView4.getMaximumVoltage()).isEqualTo(1000);
		assertThat(connectorView4.getStatus()).isEqualTo(Connector.Status.AVAILABLE);

	}

}
