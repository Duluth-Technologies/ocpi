package com.duluthtechnologies.ocpi.api.ocpi.controller.cpo.v2_1_1;

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
import com.duluthtechnologies.ocpi.api.ocpi.annotation.CPOController;
import com.duluthtechnologies.ocpi.api.ocpi.controller.cpo.CPOVersionsController;
import com.duluthtechnologies.ocpi.model.Endpoint;
import com.duluthtechnologies.ocpi.model.ModuleID;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.VersionDetails;
import com.duluthtechnologies.ocpi.model.VersionNumber;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CPO Version 2.1.1 Details")
@CPOController
@RequestMapping("/ocpi/cpo/2.1.1/")
public class CPOVersionsDetailsController {

	private static final Logger LOG = LoggerFactory.getLogger(CPOVersionsController.class);

	@Autowired
	@Qualifier("externalOcpiApiUrl")
	private String externalOcpiApiUrl;

	@GetMapping
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<VersionDetails>> getVersions() {
		LOG.debug("Returning CPO version details for version 2.1.1...");
		return ResponseEntity.ok(new Response(
				new VersionDetails(VersionNumber.V2_1_1,
						List.of(new Endpoint(ModuleID.CredentialsRegistration,
								externalOcpiApiUrl + "/ocpi/cpo/2.1.1/credentials"),
								new Endpoint(ModuleID.Locations, externalOcpiApiUrl + "/ocpi/cpo/2.1.1/locations"))),
				1000, null, Instant.now()));
	}
}
