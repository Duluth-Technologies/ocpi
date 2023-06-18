package com.duluthtechnologies.ocpi.core.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public interface LocationService {

	public record LocationForm(

			@Size(max = 36) String ocpiId,

			@NotEmpty @Size(max = 255) String name,

			@NotEmpty @Size(max = 45) String address,

			@NotEmpty @Size(max = 45) String city,

			@NotEmpty @Size(max = 10) String zipCode,

			@NotEmpty @Size(min = 3, max = 3) String countryCode,

			@Min(-90) @Max(90) double latitude,

			@Min(-180) @Max(180) double longitude,

			List<EvseForm> evses) {

	}

	public record EvseForm(

			@Size(max = 39) String ocpiId,

			@Size(max = 48) String evseId,

			@Valid List<ConnectorForm> connectors) {
	}

	public record ConnectorForm(

			@NotEmpty @Size(max = 36) String connectorId,

			@NotNull Connector.Type type,

			@NotNull Connector.Format format,

			@NotNull Connector.PowerType powerType,

			@Positive Integer maximumVoltage,

			@Positive Integer maximumAmperage,

			Connector.Status status) {
	}

	Location createLocation(LocationForm locationCreationForm);

	RegisteredCPOLocation createRegisteredCPOLocation(LocationForm locationForm, String registeredCPOKey);

	RegisteredCPOLocation updateRegisteredCPOLocation(String registeredCPOLocationKey, LocationForm locationForm);

	void patchRegisteredCPOLocation(String key, LocationForm locationForm);

	Optional<RegisteredCPOLocation> findRegisteredCPOLocation(String countryCode, String partyId, String ocpiId);

	List<RegisteredCPOLocation> findByRegisteredCpoKey(String key);

	List<Location> findLocationByOcpiId(String ocpiId);

	Page<Location> findLocation(String countryCode, String partyId, Instant dateFrom, Instant dateTo, Integer offset,
			Integer limit);

}
