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
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.SessionV211Mapper;
import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService.ChargingSessionFormWithLocation;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.Session;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "EMSP v2.1.1 Sessions")
@EMSPController
@RequestMapping("/ocpi/emsp/2.1.1/sessions")
public class EMSPSessionsController {

	private static final Logger LOG = LoggerFactory.getLogger(EMSPSessionsController.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final ChargingSessionService chargingSessionService;

	private final SessionV211Mapper sessionV211Mapper;

	public EMSPSessionsController(RegisteredOperatorService registeredOperatorService,
			ChargingSessionService chargingSessionService, SessionV211Mapper sessionV211Mapper) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.chargingSessionService = chargingSessionService;
		this.sessionV211Mapper = sessionV211Mapper;

	}

	@GetMapping("{countryCode}/{partyId}/{sessionId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response<Session>> getSession(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String sessionId) {
		LOG.debug("Getting Session with id [{}] for CPO with country code [{}] and party id [{}]...", sessionId,
				countryCode, partyId);
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot get Session with countryCode [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot get Session with countryCode [%s] and partyId [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		Optional<ChargingSession> optionalChargingSession = chargingSessionService.findChargingSession(countryCode,
				partyId, sessionId);
		if (optionalChargingSession.isEmpty()) {
			String message = "Session with id [%s] not found.".formatted(sessionId);
			LOG.error(message);
			return ResponseEntity.status(HttpStatusCode.valueOf(404))
					.body(new Response<>(null, 2001, message, Instant.now()));
		}
		Session session = sessionV211Mapper.toDTO(optionalChargingSession.get());
		return ResponseEntity.ok(new Response<>(session, 1000, null, Instant.now()));

	}

	@PutMapping("{countryCode}/{partyId}/{sessionId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> setSession(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String sessionId, @RequestBody @Valid Session session) {
		LOG.debug("Setting Session [{}] for CPO with country code [{}] and party id [{}]...", session, countryCode,
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
		Optional<ChargingSession> optionalChargingSession = chargingSessionService.findChargingSession(countryCode,
				partyId, sessionId);

		ChargingSessionFormWithLocation chargingSessionFormWithLocation = sessionV211Mapper
				.toChargingSessionFormWithLocation(session);
		if (optionalChargingSession.isEmpty()) {
			chargingSessionService.createChargingSession(registeredCPO.getKey(), chargingSessionFormWithLocation);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
		} else {
			chargingSessionService.updateChargingSession(registeredCPO.getKey(), optionalChargingSession.get().getKey(),
					chargingSessionFormWithLocation);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

	@PatchMapping("{countryCode}/{partyId}/{sessionId}")
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response> patchLocation(@PathVariable String countryCode, @PathVariable String partyId,
			@PathVariable String sessionId, @RequestBody Session session) {
		LOG.debug("Patching Session [{}] for CPO with country code [{}] and party id [{}]...", session, countryCode,
				partyId);
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();
		if (!Objects.equals(registeredCPO.getCountryCode(), countryCode)) {
			String message = "Cannot patch Session with countryCode [%s] as it doesn't match the countryCode of the registered operator in the security context."
					.formatted(countryCode);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		if (!Objects.equals(registeredCPO.getPartyId(), partyId)) {
			String message = "Cannot patch Session with countryCode [%s] and partyId [%s] as it doesn't match the partyId of the registered operator in the security context."
					.formatted(countryCode, partyId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		}
		Optional<ChargingSession> optionalChargingSession = chargingSessionService.findChargingSession(countryCode,
				partyId, sessionId);

		ChargingSessionFormWithLocation chargingSessionFormWithLocation = sessionV211Mapper
				.toChargingSessionFormWithLocation(session);
		if (optionalChargingSession.isEmpty()) {
			String message = "Cannot find Session with countryCode [%s] and partyId [%s] and OCPI id [%s]."
					.formatted(countryCode, partyId, sessionId);
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
		} else {
			chargingSessionService.patchRegisteredCPOLocation(registeredCPO.getKey(),
					optionalChargingSession.get().getKey(), chargingSessionFormWithLocation);
			Response response = new Response(null, 1000, null, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
		}
	}

}
