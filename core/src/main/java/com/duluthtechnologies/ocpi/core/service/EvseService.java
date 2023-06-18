package com.duluthtechnologies.ocpi.core.service;

import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.service.LocationService.EvseForm;

public interface EvseService {

	Optional<Evse> find(String countryCode, String partyId, String locationOcpiId, String evseOcpiId);

	Evse create(String locationKey, EvseForm evseForm);

	Evse update(String key, EvseForm evseForm);

	void patch(String key, EvseForm evseForm);

}
