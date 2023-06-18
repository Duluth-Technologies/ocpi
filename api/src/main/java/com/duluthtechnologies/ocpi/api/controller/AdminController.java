package com.duluthtechnologies.ocpi.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.duluthtechnologies.ocpi.api.dto.CPORegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.EMSPRegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.LocationCreationForm;
import com.duluthtechnologies.ocpi.api.dto.LocationView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredCPOView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPView;
import com.duluthtechnologies.ocpi.api.mapper.LocationDTOMapper;
import com.duluthtechnologies.ocpi.api.mapper.RegisteredOperatorDTOMapper;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.service.LocationService;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService.RegisteredCPOCreationForm;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService.RegisteredEMSPCreationForm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Administration")
@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final RegisteredOperatorDTOMapper registeredOperatorMapper;

	private final LocationService locationService;

	private final LocationDTOMapper locationMapper;

	public AdminController(RegisteredOperatorService registeredOperatorService,
			RegisteredOperatorDTOMapper registeredOperatorMapper, LocationService locationService,
			LocationDTOMapper locationMapper) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.registeredOperatorMapper = registeredOperatorMapper;
		this.locationService = locationService;
		this.locationMapper = locationMapper;
	}

	@Operation(summary = "Retrieve a CPO")
	@GetMapping("/cpo/{key}")
	public ResponseEntity<RegisteredCPOView> getCPO(
			@PathVariable @Parameter(description = "The key of the CPO") String key) {
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(key).orElseThrow(() -> {
			String message = "Cannot find CPO with key [%s]".formatted(key);
			LOG.error(message);
			return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
		});
		RegisteredCPOView registeredCPOView = registeredOperatorMapper.toRegisteredCPOView(registeredCPO);
		return ResponseEntity.ok(registeredCPOView);
	}

	@Operation(summary = "Register a CPO")
	@PostMapping("/cpo")
	public ResponseEntity<RegisteredCPOView> registerCPO(@Valid @RequestBody CPORegistrationForm cpoRegistrationForm) {
		RegisteredCPOCreationForm registeredCPOCreationForm = registeredOperatorMapper
				.toRegisteredCPOCreationForm(cpoRegistrationForm);
		RegisteredCPO registeredCPO = registeredOperatorService.createRegisteredCPO(registeredCPOCreationForm);
		RegisteredCPOView registeredCPOView = registeredOperatorMapper.toRegisteredCPOView(registeredCPO);
		return ResponseEntity.ok(registeredCPOView);
	}

	@Operation(summary = "Retrieves an EMSP")
	@GetMapping("/emsp/{key}")
	public ResponseEntity<RegisteredEMSPView> getEMSP(
			@PathVariable @Parameter(description = "The key of the EMSP") String key) {
		RegisteredEMSP registeredEMSP = registeredOperatorService.findEMSPByKey(key).orElseThrow(() -> {
			String message = "Cannot find EMSP with key [%s]".formatted(key);
			LOG.error(message);
			return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
		});
		RegisteredEMSPView registeredEMSPView = registeredOperatorMapper.toRegisteredEMSPView(registeredEMSP);
		return ResponseEntity.ok(registeredEMSPView);
	}

	@Operation(summary = "Register an EMSP")
	@PostMapping("/emsp")
	public ResponseEntity<RegisteredEMSPView> registerEMSP(
			@Valid @RequestBody EMSPRegistrationForm emspRegistrationForm) {
		RegisteredEMSPCreationForm registeredEMSPCreationForm = registeredOperatorMapper
				.toRegisteredEMSPCreationForm(emspRegistrationForm);
		RegisteredEMSP registeredEMSP = registeredOperatorService.createRegisteredEMSP(registeredEMSPCreationForm);
		RegisteredEMSPView registeredEMSPView = registeredOperatorMapper.toRegisteredEMSPView(registeredEMSP);
		return ResponseEntity.ok(registeredEMSPView);
	}

	@Operation(summary = "Performs the handshake with an EMSP")
	@PostMapping("/emsp/{key}/handshake")
	public ResponseEntity<RegisteredEMSPView> handshakeEMSP(
			@PathVariable @Parameter(description = "The key of the EMSP") String key) {
		LOG.debug("Performing handshake to EMSP with key [{}]...", key);
		registeredOperatorService.performHandshakeWithEMSP(key);
		RegisteredEMSP registeredEMSP = registeredOperatorService.findEMSPByKey(key).orElseThrow(() -> {
			String message = "Cannot find EMSP with key [%s]".formatted(key);
			LOG.error(message);
			return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
		});
		LOG.debug("Found EMSP of type [{}]...", registeredEMSP.getClass().getCanonicalName());
		RegisteredEMSPView registeredEMSPView = registeredOperatorMapper.toRegisteredEMSPView(registeredEMSP);
		LOG.debug("Returning EMSP of type [{}]...", registeredEMSPView.getClass().getCanonicalName());
		return ResponseEntity.ok(registeredEMSPView);
	}

	@Operation(summary = "Performs the handshake with a CPO")
	@PostMapping("/cpo/{key}/handshake")
	public ResponseEntity<RegisteredCPOView> handshakeCPO(
			@PathVariable @Parameter(description = "The key of the CPO") String key) {
		LOG.debug("Performing handshake to CPO with key [{}]...", key);
		registeredOperatorService.performHandshakeWithCPO(key);
		RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(key).orElseThrow(() -> {
			String message = "Cannot find CPO with key [%s]".formatted(key);
			LOG.error(message);
			return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
		});
		LOG.debug("Found CPO of type [{}]...", registeredCPO.getClass().getCanonicalName());
		RegisteredCPOView registeredCPOView = registeredOperatorMapper.toRegisteredCPOView(registeredCPO);
		LOG.debug("Returning CPO of type [{}]...", registeredCPOView.getClass().getCanonicalName());
		return ResponseEntity.ok(registeredCPOView);
	}

	@Operation(summary = "Creates a new Location")
	@PostMapping("/location")
	public ResponseEntity<LocationView> createLocation(@RequestBody LocationCreationForm locationCreationForm) {
		Location location = locationService.createLocation(locationMapper.toLocationForm(locationCreationForm));
		return ResponseEntity.created(null).body(locationMapper.toLocationView(location));
	}

}
