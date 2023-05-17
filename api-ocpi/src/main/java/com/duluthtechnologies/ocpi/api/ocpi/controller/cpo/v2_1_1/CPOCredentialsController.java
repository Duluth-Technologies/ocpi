package com.duluthtechnologies.ocpi.api.ocpi.controller.cpo.v2_1_1;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated.AuthenticatedType;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.CPOController;
import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.BusinessDetails;
import com.duluthtechnologies.ocpi.model.v211.Credentials;
import com.duluthtechnologies.ocpi.model.v211.Image;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "CPO v2.1.1 Credentials")
@CPOController
@RequestMapping("/ocpi/cpo/2.1.1/credentials")
public class CPOCredentialsController {

	private static final Logger LOG = LoggerFactory.getLogger(CPOCredentialsController.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final CPOInfo cpoInfo;

	private final String externalOcpiApiUrl;

	private final BusinessDetails businessDetails;

	public CPOCredentialsController(RegisteredOperatorService registeredOperatorService, CPOInfo cpoInfo,
			@Qualifier("externalOcpiApiUrl") String externalOcpiApiUrl) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.cpoInfo = cpoInfo;
		Image image = new Image(null, null, null, null, null, null);
		this.businessDetails = new BusinessDetails(cpoInfo.getName(), cpoInfo.getWebsiteUrl(), image);
		this.externalOcpiApiUrl = externalOcpiApiUrl;
	}

	@GetMapping
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<Credentials>> getCredentials() {
		// No need to check
		RegisteredEMSP registeredEMSP = registeredOperatorService.findEMSPByKey(SecurityContext.getEMSPKey()).get();
		if (!(registeredEMSP instanceof RegisteredEMSPV211)) {
			String message = "Cannot get Credentials for EMSP with name [%s] as this EMSP is not registered under version 2.1.1"
					.formatted(registeredEMSP.getName());
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(405)).body(response);
		}
		return ResponseEntity.ok(new Response(new Credentials(registeredEMSP.getIncomingToken(), externalVersionsUrl(),
				businessDetails, cpoInfo.getPartyId(), cpoInfo.getCountryCode()), 1000, null, Instant.now()));
	}

	@PostMapping
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<Credentials>> createCredentials(@RequestBody @Valid Credentials credentials) {
		RegisteredEMSP registeredEMSP = registeredOperatorService.findEMSPByKey(SecurityContext.getEMSPKey()).get();
		if (registeredEMSP instanceof RegisteredEMSPV211) {
			String message = "Cannot create Credentials for EMSP with name [%s] as Credentials have already been created."
					.formatted(registeredEMSP.getName());
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(405)).body(response);
		}
		registeredEMSP = registeredOperatorService.finalizeHandshakeWithEMSP(SecurityContext.getEMSPKey(), credentials);
		return ResponseEntity.ok(new Response(new Credentials(registeredEMSP.getIncomingToken(), externalVersionsUrl(),
				businessDetails, cpoInfo.getPartyId(), cpoInfo.getCountryCode()), 1000, null, Instant.now()));
	}

	private String externalVersionsUrl() {
		return externalOcpiApiUrl + "/ocpi/emsp/versions";
	}

}
