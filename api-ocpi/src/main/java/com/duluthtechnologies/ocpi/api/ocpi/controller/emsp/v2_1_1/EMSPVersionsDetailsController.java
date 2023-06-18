package com.duluthtechnologies.ocpi.api.ocpi.controller.emsp.v2_1_1;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated.AuthenticatedType;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.EMSPController;
import com.duluthtechnologies.ocpi.model.Endpoint;
import com.duluthtechnologies.ocpi.model.ModuleID;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.VersionDetails;
import com.duluthtechnologies.ocpi.model.VersionNumber;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "EMSP Version 2.1.1 Details")
@EMSPController
@RequestMapping("/ocpi/emsp/2.1.1/")
public class EMSPVersionsDetailsController {

	private static final Logger LOG = LoggerFactory.getLogger(EMSPVersionsDetailsController.class);

	@Autowired
	@Qualifier("externalOcpiApiUrl")
	private String externalOcpiApiUrl;

	@GetMapping
	@Authenticated(type = AuthenticatedType.CPO)
	public ResponseEntity<Response<VersionDetails>> getVersions() {
		LOG.debug("Returning EMSP version details for version 2.1.1...");
		return ResponseEntity.ok(new Response(
				new VersionDetails(VersionNumber.V2_1_1,
						List.of(new Endpoint(ModuleID.CredentialsRegistration,
								externalOcpiApiUrl + "/ocpi/emsp/2.1.1/credentials"),
								new Endpoint(ModuleID.Locations, externalOcpiApiUrl + "/ocpi/emsp/2.1.1/locations"))),
				1000, null, Instant.now()));
	}

}
