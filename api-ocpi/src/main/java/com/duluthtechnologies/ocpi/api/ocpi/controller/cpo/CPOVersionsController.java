package com.duluthtechnologies.ocpi.api.ocpi.controller.cpo;

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
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.Version;
import com.duluthtechnologies.ocpi.model.VersionNumber;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CPO Versions")
@CPOController
@RequestMapping("/ocpi/cpo/versions")
public class CPOVersionsController {

	private static final Logger LOG = LoggerFactory.getLogger(CPOVersionsController.class);

	@Autowired
	@Qualifier("externalOcpiApiUrl")
	private String externalOcpiApiUrl;

	@GetMapping
	@Authenticated(type = AuthenticatedType.EMSP)
	public ResponseEntity<Response<List<Version>>> getVersions() {
		LOG.debug("Returning CPO versions...");
		Response<List<Version>> response = new Response<>(
				List.of(new Version(VersionNumber.V2_1_1, externalOcpiApiUrl + "/ocpi/cpo/2.1.1/")), 1000, null,
				Instant.now());
		return ResponseEntity.ok(response);
	}
}
