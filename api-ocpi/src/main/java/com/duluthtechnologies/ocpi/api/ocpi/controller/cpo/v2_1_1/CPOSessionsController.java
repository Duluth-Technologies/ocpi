package com.duluthtechnologies.ocpi.api.ocpi.controller.cpo.v2_1_1;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated.AuthenticatedType;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.CPOController;
import com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1.SessionV211Mapper;
import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.Session;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CPO v2.1.1 Sessions")
@CPOController
@RequestMapping("/ocpi/cpo/2.1.1/sessions")
public class CPOSessionsController {

	private final ChargingSessionService sessionService;

	private final SessionV211Mapper sessionV211Mapper;

	private final String externalOcpiApiUrl;

	public CPOSessionsController(ChargingSessionService sessionService, SessionV211Mapper sessionV211Mapper,
			@Qualifier("externalOcpiApiUrl") String externalOcpiApiUrl) {
		super();
		this.sessionService = sessionService;
		this.sessionV211Mapper = sessionV211Mapper;
		this.externalOcpiApiUrl = externalOcpiApiUrl;
	}

	@GetMapping
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<List<Session>>> getSessions(
			@RequestParam(name = "date_from", required = false) Instant dateFrom,
			@RequestParam(name = "date_to", required = false) Instant dateTo,
			@RequestParam(defaultValue = "0", required = false) Integer offset,
			@RequestParam(defaultValue = "100", required = false) Integer limit) {
		if (limit > 100) {
			limit = 100;
		}
		Page<RegisteredEMSPChargingSession> sessionPage = sessionService
				.findRegisteredEMSPSessions(SecurityContext.getEMSPKey(), dateFrom, dateTo, offset, limit);
		List<Session> sessionsV211 = sessionV211Mapper.toSessions(sessionPage.content());
		HttpHeaders responseHeaders = new HttpHeaders();
		if ((offset + limit) < sessionPage.total()) {
			String link = externalOcpiApiUrl
					+ "/ocpi/cpo/2.1.1/locations?offset=%s&limit=%s".formatted(offset + limit, limit);
			responseHeaders.set("Link", link);
		}
		responseHeaders.set("X-Total-Count", Integer.toString(sessionPage.total()));
		responseHeaders.set("X-Limit", Integer.toString(sessionPage.content().size()));
		return ResponseEntity.ok().headers(responseHeaders)
				.body(new Response<>(sessionsV211, 1000, null, Instant.now()));

	}
}
