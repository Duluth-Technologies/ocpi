package com.duluthtechnologies.ocpi.core.store;

import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Status;

public interface ConnectorStore {

	Connector createConnector(String evseKey, Connector connector);

	void delete(String key);

	Connector update(String key, Connector connector);

	Connector patch(String key, Connector connector);

	Optional<Connector> findByCountryCodeAndPartyIdAndLocationIdAndEvseId(String countryCode, String partyId,
			String locationId, String evseId);

	Connector updateStatus(String key, Status updatedStatus);

	Optional<Connector> findByKey(String key);

}
