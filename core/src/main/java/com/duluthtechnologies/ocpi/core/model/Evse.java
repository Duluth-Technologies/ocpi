package com.duluthtechnologies.ocpi.core.model;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public interface Evse {

	@Size(max = 255)
	@NotEmpty
	String getKey();

	@NotEmpty
	@Size(max = 39)
	String getOcpiId();

	@NotEmpty
	@Size(max = 48)
	String getEvseId();

	@NotNull
	@Past
	Instant getLastModifiedDate();

	@NotNull
	@Valid
	Location getLocation();

	List<Connector> getConnectors();

}
