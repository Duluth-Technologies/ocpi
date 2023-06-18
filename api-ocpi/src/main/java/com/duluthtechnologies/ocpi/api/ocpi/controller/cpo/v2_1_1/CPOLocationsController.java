package com.duluthtechnologies.ocpi.api.ocpi.controller.cpo.v2_1_1;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated.AuthenticatedType;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.CPOController;
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.ConnectorV211Mapper;
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.EvseV211Mapper;
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.LocationV211Mapper;
import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.service.ConnectorService;
import com.duluthtechnologies.ocpi.core.service.EvseService;
import com.duluthtechnologies.ocpi.core.service.LocationService;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Location;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CPO v2.1.1 Locations")
@CPOController
@RequestMapping("/ocpi/cpo/2.1.1/locations")
public class CPOLocationsController {

	private static final Logger LOG = LoggerFactory.getLogger(CPOLocationsController.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final LocationService locationService;

	private final EvseService evseService;

	private final ConnectorService connectorService;

	private final LocationV211Mapper locationMapper;

	private final EvseV211Mapper evseMapper;

	private final ConnectorV211Mapper connectorMapper;

	private final CPOInfo cpoInfo;

	private final String externalOcpiApiUrl;

	public CPOLocationsController(RegisteredOperatorService registeredOperatorService, LocationService locationService,
			LocationV211Mapper locationMapper, EvseV211Mapper evseMapper, EvseService evseService,
			ConnectorV211Mapper connectorMapper, ConnectorService connectorService, CPOInfo cpoInfo,
			@Qualifier("externalOcpiApiUrl") String externalOcpiApiUrl) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.locationService = locationService;
		this.evseService = evseService;
		this.connectorService = connectorService;
		this.locationMapper = locationMapper;
		this.evseMapper = evseMapper;
		this.connectorMapper = connectorMapper;
		this.cpoInfo = cpoInfo;
		this.externalOcpiApiUrl = externalOcpiApiUrl;
	}

	@GetMapping
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<List<Location>>> getLocations(
			@RequestParam(name = "date_from", required = false) Instant dateFrom,
			@RequestParam(name = "date_to", required = false) Instant dateTo,
			@RequestParam(defaultValue = "0", required = false) Integer offset,
			@RequestParam(defaultValue = "100", required = false) Integer limit) {
		if (limit > 100) {
			limit = 100;
		}
		Page<com.duluthtechnologies.ocpi.core.model.Location> locationPage = locationService
				.findLocation(cpoInfo.getCountryCode(), cpoInfo.getPartyId(), dateFrom, dateTo, offset, limit);
		List<Location> locationsV211 = locationMapper.toLocations(locationPage.content());
		HttpHeaders responseHeaders = new HttpHeaders();
		if ((offset + limit) < locationPage.total()) {
			String link = externalOcpiApiUrl
					+ "/ocpi/cpo/2.1.1/locations?offset=%s&limit=%s".formatted(offset + limit, limit);
			responseHeaders.set("Link", link);
		}
		responseHeaders.set("X-Total-Count", Integer.toString(locationPage.total()));
		responseHeaders.set("X-Limit", Integer.toString(locationPage.content().size()));
		return ResponseEntity.ok().headers(responseHeaders)
				.body(new Response<>(locationsV211, 1000, null, Instant.now()));

	}

	@GetMapping("{countryCode}/{partyId}/{locationId}")
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<Location>> getLocation(@PathVariable String countryCode,
			@PathVariable String partyId, @PathVariable String locationId) {
		if (!cpoInfo.getCountryCode().equals(countryCode)) {
			String message = "Requested country code [%s] doesn't match country code [%s]".formatted(countryCode,
					cpoInfo.getCountryCode());
			LOG.error(message);
			return ResponseEntity.badRequest().body(new Response<>(null, 2001, message, Instant.now()));
		}
		if (!cpoInfo.getPartyId().equals(partyId)) {
			String message = "Requested party id [%s] doesn't match party id [%s]".formatted(partyId,
					cpoInfo.getPartyId());
			LOG.error(message);
			return ResponseEntity.badRequest().body(new Response<>(null, 2001, message, Instant.now()));
		}
		List<com.duluthtechnologies.ocpi.core.model.Location> locations = locationService
				.findLocationByOcpiId(locationId);
		Optional<com.duluthtechnologies.ocpi.core.model.Location> locationOptional = locations.stream()
				.filter(l -> !(l instanceof RegisteredCPOLocation)).findFirst();
		if (locationOptional.isEmpty()) {
			String message = "Location with id [%s] not found.".formatted(locationId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		Location location = locationMapper.toLocation(locationOptional.get());
		return ResponseEntity.ok(new Response<>(location, 1000, null, Instant.now()));

	}

	@GetMapping("{countryCode}/{partyId}/{locationId}/{evseId}")
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<EVSE>> getEvse(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId) {
		if (!cpoInfo.getCountryCode().equals(countryCode)) {
			String message = "Requested country code [%s] doesn't match country code [%s]".formatted(countryCode,
					cpoInfo.getCountryCode());
			LOG.error(message);
			return ResponseEntity.badRequest().body(new Response<>(null, 2001, message, Instant.now()));
		}
		if (!cpoInfo.getPartyId().equals(partyId)) {
			String message = "Requested party id [%s] doesn't match party id [%s]".formatted(partyId,
					cpoInfo.getPartyId());
			LOG.error(message);
			return ResponseEntity.badRequest().body(new Response<>(null, 2001, message, Instant.now()));
		}
		List<com.duluthtechnologies.ocpi.core.model.Location> locations = locationService
				.findLocationByOcpiId(locationId);
		Optional<com.duluthtechnologies.ocpi.core.model.Location> locationOptional = locations.stream()
				.filter(l -> !(l instanceof RegisteredCPOLocation)).findFirst();
		if (locationOptional.isEmpty()) {
			String message = "Location with id [%s] not found.".formatted(locationId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		Optional<Evse> evseOptional = locationOptional.get().getEvses().stream()
				.filter(evse -> evse.getOcpiId().equals(evseId)).findFirst();
		if (evseOptional.isEmpty()) {
			String message = "Evse with id [%s] not found".formatted(evseId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		EVSE evse = evseMapper.toEvse(evseOptional.get());
		return ResponseEntity.ok(new Response<>(evse, 1000, null, Instant.now()));
	}

	@GetMapping("{countryCode}/{partyId}/{locationId}/{evseId}/{connectorId}")
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response> setConnector(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String locationId, @PathVariable String evseId, @PathVariable String connectorId) {
		if (!cpoInfo.getCountryCode().equals(countryCode)) {
			String message = "Requested country code [%s] doesn't match country code [%s]".formatted(countryCode,
					cpoInfo.getCountryCode());
			LOG.error(message);
			return ResponseEntity.badRequest().body(new Response<>(null, 2001, message, Instant.now()));
		}
		if (!cpoInfo.getPartyId().equals(partyId)) {
			String message = "Requested party id [%s] doesn't match party id [%s]".formatted(partyId,
					cpoInfo.getPartyId());
			LOG.error(message);
			return ResponseEntity.badRequest().body(new Response<>(null, 2001, message, Instant.now()));
		}
		List<com.duluthtechnologies.ocpi.core.model.Location> locations = locationService
				.findLocationByOcpiId(locationId);
		Optional<com.duluthtechnologies.ocpi.core.model.Location> locationOptional = locations.stream()
				.filter(l -> !(l instanceof RegisteredCPOLocation)).findFirst();
		if (locationOptional.isEmpty()) {
			String message = "Location with id [%s] not found.".formatted(locationId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		Optional<Evse> evseOptional = locationOptional.get().getEvses().stream()
				.filter(evse -> evse.getOcpiId().equals(evseId)).findFirst();
		if (evseOptional.isEmpty()) {
			String message = "Evse with id [%s] not found".formatted(evseId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		Optional<com.duluthtechnologies.ocpi.core.model.Connector> connectorOptional = evseOptional.get()
				.getConnectors().stream().filter(c -> c.getConnectorId().equals(connectorId)).findFirst();
		if (connectorOptional.isEmpty()) {
			String message = "Connector with id [%s] not found".formatted(connectorId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		com.duluthtechnologies.ocpi.model.v211.Connector connector = connectorMapper
				.toConnector(connectorOptional.get());
		return ResponseEntity.ok(new Response<>(connector, 1000, null, Instant.now()));
	}

}
