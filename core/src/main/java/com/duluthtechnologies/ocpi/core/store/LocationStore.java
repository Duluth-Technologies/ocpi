package com.duluthtechnologies.ocpi.core.store;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;

public interface LocationStore {

	Location createLocation(Location location);

	RegisteredCPOLocation createRegisteredCPOLocation(RegisteredCPOLocation location, String registeredCPOKey);

	Location getByKey(String key);

	Optional<RegisteredCPOLocation> findByCountryCodeAndPartyIdAndOcpiId(String countryCode, String partyId,
			String ocpiId);

	RegisteredCPOLocation updateRegisteredCPOLocation(RegisteredCPOLocation location);

	RegisteredCPOLocation patchRegisteredCPOLocation(RegisteredCPOLocation location);

	List<RegisteredCPOLocation> findByRegisteredCpoKey(String key);

	List<Location> findByOcpiId(String ocpiId);

	Page<Location> findNotRegisteredLocations(Instant dateFrom, Instant dateTo, Integer offset, Integer limit);

	Optional<RegisteredCPOLocation> findByRegisteredCpoKeyAndOcpiId(String registeredCpoKey, String locationOcpiId);

}
