package com.duluthtechnologies.ocpi.api.ocpi.controller.emsp.v2_1_1;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated.AuthenticatedType;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.EMSPController;
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.ConnectorV211Mapper;
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.EvseV211Mapper;
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.LocationV211Mapper;
import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.service.ConnectorService;
import com.duluthtechnologies.ocpi.core.service.EvseService;
import com.duluthtechnologies.ocpi.core.service.LocationService;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.EvseForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.LocationForm;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.Connector;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Location;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "EMSP v2.1.1 Locations")
@EMSPController
@RequestMapping("/ocpi/emsp/2.1.1/locations")
public class EMSPLocationsController {

	private static final Logger LOG = LoggerFactory.getLogger(EMSPLocationsController.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final LocationService locationService;

	private final EvseService evseService;

	private final ConnectorService connectorService;

	private final LocationV211Mapper locationMapper;

	private final EvseV211Mapper evseMapper;

	private final ConnectorV211Mapper connectorMapper;

	public EMSPLocationsController(RegisteredOperatorService registeredOperatorService, LocationService locationService,
			LocationV211Mapper locationMapper, EvseV211Mapper evseMapper, EvseService evseService,
			ConnectorV211Mapper connectorMapper, ConnectorService connectorService) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.locationService = locationService;
		this.evseService = evseService;
		this.connectorService = connectorService;
		this.locationMapper = locationMapper;
		this.evseMapper = evseMapper;
		this.connectorMapper = connectorMapper;
	}

	@GetMapping("{countryCode}/{partyId}/{locationId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response<Location>> getLocation(@PathVariable String countryCode,
			@PathVariable String partyId, @PathVariable String locationId) {
		LOG.debug("Getting Location with id [{}] for CPO with country code [{}] and party id [{}]...", locationId,
				countryCode, partyId);
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot get Location with countryCode [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot get Location with countryCode [%s] and partyId [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Location with id [%s] not found.".formatted(locationId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		Location location = locationMapper.toLocation(optionalRegisteredCPOLocation.get());
		return ResponseEntity.ok(new Response<>(location, 1000, null, Instant.now()));

	}

	@PutMapping("{countryCode}/{partyId}/{locationId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> setLocation(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @RequestBody @Valid Location location) {
		LOG.debug("Setting Location [{}] for CPO with country code [{}] and party id [{}]...", location, countryCode,
				partyId);
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot set Location with countryCode [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot set Location with countryCode [%s] and partyId [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		LocationForm locationForm = locationMapper.toLocationForm(location);
		if (optionalRegisteredCPOLocation.isEmpty()) {
			locationService.createRegisteredCPOLocation(locationForm, SecurityContext.getCPOKey());
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
		} else {
			locationService.updateRegisteredCPOLocation(optionalRegisteredCPOLocation.get().getKey(), locationForm);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

	@PatchMapping("{countryCode}/{partyId}/{locationId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> patchLocation(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @RequestBody Location location) {
		LOG.debug("Patching Location [{}] for CPO with country code [{}] and party id [{}]...", location, countryCode,
				partyId);
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot patch Location with countryCode [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot patch Location with countryCode [%s] and partyId [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		LocationForm locationForm = locationMapper.toLocationForm(location);
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Cannot find Location with countryCode [%s] and partyId [%s] and OCPI id [%s]."
					.formatted(countryCode, partyId, locationId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		} else {
			locationService.patchRegisteredCPOLocation(optionalRegisteredCPOLocation.get().getKey(), locationForm);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

	@GetMapping("{countryCode}/{partyId}/{locationId}/{evseId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response<EVSE>> getEvse(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId) {
		// Retrieve the Registered CPO making the call
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();

		// Make sure country code matches
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot get EVSE with country code [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Make sure party id matches
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot get EVSE with country code [%s] and party id [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Look for the Location with the given id
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		// If the Location has not been found then return an error
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Cannot get EVSE with country code [%s] and party id [%s] and Location with id [%s] as no such Location exists."
					.formatted(countryCode, partyId, locationId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		Optional<Evse> optionalEvse = optionalRegisteredCPOLocation.get().getEvses().stream()
				.filter(evse -> evse.getOcpiId().equals(evseId)).findFirst();
		if (optionalEvse.isEmpty()) {
			String message = "Evse with id [%s] not found".formatted(evseId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		EVSE evse = evseMapper.toEvse(optionalEvse.get());
		return ResponseEntity.ok(new Response<>(evse, 1000, null, Instant.now()));
	}

	@PutMapping("{countryCode}/{partyId}/{locationId}/{evseId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> setEvse(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId, @RequestBody @Valid EVSE evse) {
		// Retrieve the Registered CPO making the call
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();

		// Make sure country code matches
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot set EVSE with country code [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Make sure party id matches
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot set EVSE with country code [%s] and party id [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Look for the Location with the given id
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		// If the Location has not been found then return an error
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Cannot set EVSE with country code [%s] and party id [%s] and Location with id [%s] as no such Location exists."
					.formatted(countryCode, partyId, locationId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		// Check if the EVSE already exists
		Optional<Evse> optionalEvse = evseService.find(countryCode, partyId, locationId, evseId);

		EvseForm evseForm = evseMapper.toEvseForm(evse);
		if (optionalEvse.isEmpty()) {
			evseService.create(optionalRegisteredCPOLocation.get().getKey(), evseForm);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
		} else {
			evseService.update(optionalEvse.get().getKey(), evseForm);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

	@PatchMapping("{countryCode}/{partyId}/{locationId}/{evseId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> patchEvse(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId, @RequestBody EVSE evse) {
		LOG.debug(
				"Patching Evse with id [{}] of Location with id [{}] of CPO with country code [{}] and party id [{}] with content [{}]...",
				evseId, locationId, countryCode, partyId, evse);
		// Retrieve the Registered CPO making the call
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();

		// Make sure country code matches
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot patch EVSE with country code [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Make sure party id matches
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot patch EVSE with country code [%s] and party id [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Look for the Location with the given id
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		// If the Location has not been found then return an error
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Cannot patch EVSE with country code [%s] and party id [%s] and Location with id [%s] as no such Location exists."
					.formatted(countryCode, partyId, locationId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		// Check if the EVSE already exists
		Optional<Evse> optionalEvse = evseService.find(countryCode, partyId, locationId, evseId);

		EvseForm evseForm = evseMapper.toEvseForm(evse);
		if (optionalEvse.isEmpty()) {
			String message = "Cannot find Evse with countryCode [%s] and partyId [%s] and locationId [%s] and evseId [%s]."
					.formatted(countryCode, partyId, locationId, evseId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		} else {
			evseService.patch(optionalEvse.get().getKey(), evseForm);
			if (evse.status() != null) {
				optionalEvse.get().getConnectors().stream().forEach(c -> {
					connectorService.patch(c.getKey(), connectorMapper.toConnectorForm(null, evse.status()));
				});
			}
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

	@GetMapping("{countryCode}/{partyId}/{locationId}/{evseId}/{connectorId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> getConnector(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId, @PathVariable String connectorId) {
		// Retrieve the Registered CPO making the call
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();

		// Make sure country code matches
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot get Connector with country code [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Make sure party id matches
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot set Connector with country code [%s] and party id [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Look for the Location with the given id
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		// If the Location has not been found then return an error
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Cannot set Connector with country code [%s] and party id [%s] and Location with id [%s] as no such Location exists."
					.formatted(countryCode, partyId, locationId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		// Look to the EVSE with the given id
		Optional<Evse> optionalEvse = evseService.find(countryCode, partyId, locationId, evseId);
		// If the EVSE has not been found then return an error
		if (optionalEvse.isEmpty()) {
			String message = "Cannot set Connector with country code [%s] and party id [%s] and Location with id [%s] and EVSE with id [%s] as no such EVSE exists."
					.formatted(countryCode, partyId, locationId, evseId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		Optional<com.duluthtechnologies.ocpi.core.model.Connector> optionalConnector = connectorService
				.find(countryCode, partyId, locationId, evseId, connectorId);
		if (optionalConnector.isEmpty()) {
			String message = "Connector with id [%s] not found".formatted(connectorId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		Connector connector = connectorMapper.toConnector(optionalConnector.get());
		return ResponseEntity.ok(new Response<>(connector, 1000, null, Instant.now()));
	}

	@PutMapping("{countryCode}/{partyId}/{locationId}/{evseId}/{connectorId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> setConnector(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId, @PathVariable String connectorId,
			@RequestBody @Valid Connector connector) {
		// Retrieve the Registered CPO making the call
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();

		// Make sure country code matches
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot set Connector with country code [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Make sure party id matches
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot set Connector with country code [%s] and party id [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Look for the Location with the given id
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		// If the Location has not been found then return an error
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Cannot set Connector with country code [%s] and party id [%s] and Location with id [%s] as no such Location exists."
					.formatted(countryCode, partyId, locationId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		// Look to the EVSE with the given id
		Optional<Evse> optionalEvse = evseService.find(countryCode, partyId, locationId, evseId);
		// If the EVSE has not been found then return an error
		if (optionalEvse.isEmpty()) {
			String message = "Cannot set Connector with country code [%s] and party id [%s] and Location with id [%s] and EVSE with id [%s] as no such EVSE exists."
					.formatted(countryCode, partyId, locationId, evseId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		Optional<com.duluthtechnologies.ocpi.core.model.Connector> optionalConnector = connectorService
				.find(countryCode, partyId, locationId, evseId, connectorId);
		if (optionalConnector.isEmpty()) {
			ConnectorForm connectorForm = connectorMapper.toConnectorForm(connector,
					(com.duluthtechnologies.ocpi.model.v211.Status) null);
			connectorService.create(optionalEvse.get().getKey(), connectorForm);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
		} else {
			// OCPI v2.1.1 doesn't allow to pass the connector status, so we just return set
			// the previous status into the form
			ConnectorForm connectorForm = connectorMapper.toConnectorForm(connector,
					optionalConnector.get().getStatus());
			connectorService.update(optionalEvse.get().getKey(), connectorForm);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

	@PatchMapping("{countryCode}/{partyId}/{locationId}/{evseId}/{connectorId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> patchConnector(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId, @PathVariable String connectorId,
			@RequestBody Connector connector) {
		// Retrieve the Registered CPO making the call
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();

		// Make sure country code matches
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot patch Connector with country code [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Make sure party id matches
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot patch Connector with country code [%s] and party id [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}

		// Look for the Location with the given id
		Optional<RegisteredCPOLocation> optionalRegisteredCPOLocation = locationService
				.findRegisteredCPOLocation(countryCode, partyId, locationId);

		// If the Location has not been found then return an error
		if (optionalRegisteredCPOLocation.isEmpty()) {
			String message = "Cannot patch Connector with country code [%s] and party id [%s] and Location with id [%s] as no such Location exists."
					.formatted(countryCode, partyId, locationId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		// Look to the EVSE with the given id
		Optional<Evse> optionalEvse = evseService.find(countryCode, partyId, locationId, evseId);
		// If the EVSE has not been found then return an error
		if (optionalEvse.isEmpty()) {
			String message = "Cannot patch Connector with country code [%s] and party id [%s] and Location with id [%s] and EVSE with id [%s] as no such EVSE exists."
					.formatted(countryCode, partyId, locationId, evseId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		}

		Optional<com.duluthtechnologies.ocpi.core.model.Connector> optionalConnector = connectorService
				.find(countryCode, partyId, locationId, evseId, connectorId);
		if (optionalConnector.isEmpty()) {
			String message = "Cannot find Connector with countryCode [%s] and partyId [%s] and locationId [%s] and evseId [%s] and connectorId [%s]."
					.formatted(countryCode, partyId, locationId, evseId, connectorId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		} else {
			// OCPI v2.1.1 doesn't allow to pass the connector status, so we just return set
			// the previous status into the form
			ConnectorForm connectorForm = connectorMapper.toConnectorForm(connector,
					optionalConnector.get().getStatus());
			connectorService.patch(optionalEvse.get().getKey(), connectorForm);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

}
