package com.duluthtechnologies.ocpi.api.ocpi.controller.emsp.v2_1_1;

import java.time.Instant;
import java.util.stream.Stream;

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
import com.duluthtechnologies.ocpi.api.ocpi.annotation.EMSPController;
import com.duluthtechnologies.ocpi.core.configuration.EMSPInfo;
import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.Version;
import com.duluthtechnologies.ocpi.model.VersionNumber;
import com.duluthtechnologies.ocpi.model.v211.BusinessDetails;
import com.duluthtechnologies.ocpi.model.v211.Credentials;
import com.duluthtechnologies.ocpi.model.v211.Image;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "EMSP v2.1.1 Credentials")
@EMSPController
@RequestMapping("/ocpi/emsp/2.1.1/credentials")
public class EMSPCredentialsController {

	private static final Logger LOG = LoggerFactory.getLogger(EMSPCredentialsController.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final EMSPInfo emspInfo;

	private final BusinessDetails businessDetails;

	private final String externalOcpiApiUrl;

	public EMSPCredentialsController(RegisteredOperatorService registeredOperatorService, EMSPInfo emspInfo,
			@Qualifier("externalOcpiApiUrl") String externalOcpiApiUrl) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.emspInfo = emspInfo;
		Image image = new Image(null, null, null, null, null, null);
		this.businessDetails = new BusinessDetails(emspInfo.getName(), emspInfo.getWebsiteUrl(), image);
		this.externalOcpiApiUrl = externalOcpiApiUrl;
	}

	@GetMapping
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response<Credentials>> getCredentials() {
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();
		if (!(registeredCPO instanceof RegisteredCPOV211)) {
			String message = "Cannot get Credentials for CPO with name [%s] as this CPO is not registered under version 2.1.1"
					.formatted(registeredCPO.getName());
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(405)).body(response);
		}
		return ResponseEntity.ok(new Response(
				new Credentials(null, null, businessDetails, emspInfo.getPartyId(), emspInfo.getCountryCode()), 1000,
				null, Instant.now()));
	}

	@PostMapping
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response<Credentials>> createCredentials(@RequestBody @Valid Credentials credentials) {
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(SecurityContext.getCPOKey()).get();
		if (registeredCPO instanceof RegisteredCPOV211) {
			String message = "Cannot create Credentials for EMSP with name[%s] as Credentials have already been created."
					.formatted(registeredCPO.getName());
			LOG.error(message);
			Response response = new Response(null, 3001, message, Instant.now());
			return ResponseEntity.status(HttpStatusCode.valueOf(405)).body(response);
		}
		registeredCPO = registeredOperatorService.finalizeHandshakeWithCPO(SecurityContext.getCPOKey(), credentials);
		return ResponseEntity.ok(new Response(new Credentials(registeredCPO.getIncomingToken(), externalVersionsUrl(),
				businessDetails, emspInfo.getPartyId(), emspInfo.getCountryCode()), 1000, null, Instant.now()));
	}

	private String externalVersionsUrl() {
		return externalOcpiApiUrl + "/ocpi/cpo/versions";
	}

	private Version pickVersion(Version[] versions) {
		return Stream.of(versions).filter(v -> v.version() == VersionNumber.V2_1_1).findFirst().orElseThrow();
	}

}
