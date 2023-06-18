package com.duluthtechnologies.ocpi.core.store;

import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.Evse;

public interface EvseStore {

	Evse createEVSE(String locationKey, Evse evse);

	Evse updateEVSE(String evseKey, Evse evse);

	Evse patchEVSE(String key, Evse evse);

	void delete(String key);

	Optional<Evse> findByCountryCodeAndPartyIdAndLocationOcpiIdAndEvseOcpiId(String countryCode, String partyId,
			String locationOcpiId, String evseOcpiId);

	Optional<Evse> findByKey(String key);

}
