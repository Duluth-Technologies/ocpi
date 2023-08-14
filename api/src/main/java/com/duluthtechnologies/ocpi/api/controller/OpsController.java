package com.duluthtechnologies.ocpi.api.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duluthtechnologies.ocpi.api.dto.CPOLocationView;
import com.duluthtechnologies.ocpi.api.dto.ChargingSessionView;
import com.duluthtechnologies.ocpi.api.dto.ConnectorView;
import com.duluthtechnologies.ocpi.api.dto.LocationView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPChargingSessionForm;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPChargingSessionView;
import com.duluthtechnologies.ocpi.api.mapper.ChargingSessionDTOMapper;
import com.duluthtechnologies.ocpi.api.mapper.ConnectorDTOMapper;
import com.duluthtechnologies.ocpi.api.mapper.LocationDTOMapper;
import com.duluthtechnologies.ocpi.api.mapper.RegisteredOperatorDTOMapper;
import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService.RegisteredEMSPChargingSessionCreationForm;
import com.duluthtechnologies.ocpi.core.service.ConnectorService;
import com.duluthtechnologies.ocpi.core.service.EmspService;
import com.duluthtechnologies.ocpi.core.service.LocationService;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Operations")
@Validated
@RestController
@RequestMapping("/api/ops")
public class OpsController {

	private static final Logger LOG = LoggerFactory.getLogger(OpsController.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final Optional<EmspService> emspService;

	private final RegisteredOperatorDTOMapper registeredOperatorMapper;

	private final LocationService locationService;

	private final LocationDTOMapper locationMapper;

	private final ConnectorService connectorService;

	private final ChargingSessionService chargingSessionService;

	private final ConnectorDTOMapper connectorDTOMapper;

	private final ChargingSessionDTOMapper chargingSessionDTOMapper;

	public OpsController(RegisteredOperatorService registeredOperatorService,
			RegisteredOperatorDTOMapper registeredOperatorMapper, LocationService locationService,
			LocationDTOMapper locationMapper, ConnectorService connectorService, ConnectorDTOMapper connectorDTOMapper,
			Optional<EmspService> emspService, ChargingSessionService chargingSessionService,
			ChargingSessionDTOMapper chargingSessionDTOMapper) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.emspService = emspService;
		this.registeredOperatorMapper = registeredOperatorMapper;
		this.locationService = locationService;
		this.locationMapper = locationMapper;
		this.connectorService = connectorService;
		this.chargingSessionService = chargingSessionService;
		this.connectorDTOMapper = connectorDTOMapper;
		this.chargingSessionDTOMapper = chargingSessionDTOMapper;
	}

	@Operation(summary = "Retrieve Locations of a CPO")
	@GetMapping("/cpo/{key}/locations")
	public ResponseEntity<List<CPOLocationView>> getCPOLocations(
			@PathVariable @Parameter(description = "The key of the CPO") String key) {
		List<RegisteredCPOLocation> registeredCPOLocations = locationService.findByRegisteredCpoKey(key);
		List<CPOLocationView> cpoLocationViews = registeredCPOLocations.stream().map(locationMapper::toCpoLocationView)
				.toList();
		return ResponseEntity.ok(cpoLocationViews);
	}

	@Operation(summary = "Retrieve Connector")
	@GetMapping("/connector/{key}")
	public ResponseEntity<ConnectorView> getConnector(
			@PathVariable @Parameter(description = "The key of the Connector") String key,
			@RequestParam(defaultValue = "false", required = false) boolean refresh) {
		Connector connector;
		if (refresh) {
			connector = connectorService.refreshStatus(key);
		} else {
			connector = connectorService.getByKey(key);
		}
		ConnectorView connectorView = connectorDTOMapper.toConnectorView(connector);
		return ResponseEntity.ok(connectorView);
	}

	@Operation(summary = "Update Connector status")
	@PostMapping("/connector/{key}/status/{status}")
	public ResponseEntity<Void> setConnectorStatus(
			@PathVariable @Parameter(description = "The key of the Connector") String key,
			@PathVariable Connector.Status status) {
		connectorService.setStatus(key, status);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Return a Location as it is stored on a given Registered EMSP")
	@GetMapping("emsp/{emspKey}/location/{locationKey}")
	public ResponseEntity<LocationView> getLocation(@PathVariable String emspKey, @PathVariable String locationKey) {
		Location location = emspService.get().getLocation(emspKey, locationKey);
		LocationView locationView = locationMapper.toLocationView(location);
		return ResponseEntity.ok(locationView);
	}

	@Operation(summary = "Create Charging Session")
	@PostMapping("/charging-session")
	public ResponseEntity<RegisteredEMSPChargingSessionView> createChargingSession(
			@RequestBody RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm) {
		LOG.info("Creating Registered EMSP Charging Session [{}]...", registeredEMSPChargingSessionForm);
		RegisteredEMSPChargingSessionCreationForm registeredEMSPChargingSessionCreationForm = chargingSessionDTOMapper
				.toRegisteredEMSPChargingSessionCreationForm(registeredEMSPChargingSessionForm);
		RegisteredEMSPChargingSession registeredEMSPChargingSession = chargingSessionService
				.createRegisteredEMSPChargingSession(registeredEMSPChargingSessionCreationForm);
		RegisteredEMSPChargingSessionView registeredEMSPChargingSessionView = chargingSessionDTOMapper
				.toRegisteredEMSPChargingSessionView(registeredEMSPChargingSession);
		return ResponseEntity.ok(registeredEMSPChargingSessionView);
	}

	@Operation(summary = "Update Charging Session")
	@PutMapping("/charging-session")
	public ResponseEntity<RegisteredEMSPChargingSessionView> updateChargingSession(
			@RequestBody RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm) {
		com.duluthtechnologies.ocpi.core.service.ChargingSessionService.RegisteredEMSPChargingSessionForm chargingSessionServiceRegisteredEMSPChargingSessionForm = chargingSessionDTOMapper
				.toRegisteredEMSPChargingSessionForm(registeredEMSPChargingSessionForm);
		RegisteredEMSPChargingSession registeredEMSPChargingSession = chargingSessionService
				.updateRegisteredEMSPChargingSession(chargingSessionServiceRegisteredEMSPChargingSessionForm);
		RegisteredEMSPChargingSessionView registeredEMSPChargingSessionView = chargingSessionDTOMapper
				.toRegisteredEMSPChargingSessionView(registeredEMSPChargingSession);
		return ResponseEntity.ok(registeredEMSPChargingSessionView);
	}

	@Operation(summary = "Get Charging Session")
	@GetMapping("/charging-session")
	public ResponseEntity<List<ChargingSessionView>> getChargingSession(@RequestParam Instant dateFrom, Instant dateTo,
			@RequestParam(required = false) String connectorKey) {
		List<ChargingSession> chargingSessions = chargingSessionService.findChargingSessions(dateFrom, dateTo,
				Optional.ofNullable(connectorKey));
		List<ChargingSessionView> chargingSessionViews = chargingSessions.stream()
				.map(chargingSessionDTOMapper::toChargingSessionView).toList();
		return ResponseEntity.ok(chargingSessionViews);
	}

	@Operation(summary = "Get Charging Session by key")
	@GetMapping("/charging-session/{key}")
	public ResponseEntity<ChargingSessionView> getChargingSessionByKey(@PathVariable String key) {
		ChargingSession chargingSession = chargingSessionService.getByKey(key);
		ChargingSessionView chargingSessionView = chargingSessionDTOMapper.toChargingSessionView(chargingSession);
		return ResponseEntity.ok(chargingSessionView);
	}

}
