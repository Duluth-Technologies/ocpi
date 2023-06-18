package com.duluthtechnologies.ocpi.core.model;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public interface Location {

	@Size(max = 255)
	@NotEmpty
	String getKey();

	@NotEmpty
	@Size(max = 39)
	String getOcpiId();

	@Size(max = 255)
	String getName();

	@NotEmpty
	@Size(max = 45)
	String getAddress();

	@NotEmpty
	@Size(max = 45)
	String getCity();

	@NotEmpty
	@Size(max = 10)
	String getZipCode();

	@NotEmpty
	@Size(min = 3, max = 3)
	String getCountryCode();

	@Min(-90)
	@Max(90)
	double getLatitude();

	@Min(-90)
	@Max(90)
	double getLongitude();

	@NotNull
	@Past
	Instant getLastModifiedDate();

	@Valid
	List<Evse> getEvses();

}
