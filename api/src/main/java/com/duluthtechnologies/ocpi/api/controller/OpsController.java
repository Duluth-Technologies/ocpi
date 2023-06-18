package com.duluthtechnologies.ocpi.api.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duluthtechnologies.ocpi.api.dto.CPOLocationView;
import com.duluthtechnologies.ocpi.api.dto.ConnectorView;
import com.duluthtechnologies.ocpi.api.dto.LocationView;
import com.duluthtechnologies.ocpi.api.mapper.ConnectorDTOMapper;
import com.duluthtechnologies.ocpi.api.mapper.LocationDTOMapper;
import com.duluthtechnologies.ocpi.api.mapper.RegisteredOperatorDTOMapper;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
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

	private final ConnectorDTOMapper connectorDTOMapper;

	public OpsController(RegisteredOperatorService registeredOperatorService,
			RegisteredOperatorDTOMapper registeredOperatorMapper, LocationService locationService,
			LocationDTOMapper locationMapper, ConnectorService connectorService, ConnectorDTOMapper connectorDTOMapper,
			Optional<EmspService> emspService) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.emspService = emspService;
		this.registeredOperatorMapper = registeredOperatorMapper;
		this.locationService = locationService;
		this.locationMapper = locationMapper;
		this.connectorService = connectorService;
		this.connectorDTOMapper = connectorDTOMapper;
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

}
