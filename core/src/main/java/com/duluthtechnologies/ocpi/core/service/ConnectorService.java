package com.duluthtechnologies.ocpi.core.service;

import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Status;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;

public interface ConnectorService {

	Optional<Connector> find(String countryCode, String partyId, String locationId, String evseId, String connectorId);

	Connector create(String evseKey, ConnectorForm connectorForm);

	Connector update(String key, ConnectorForm connectorForm);

	void patch(String key, ConnectorForm connectorForm);

	void delete(String key);

	Connector refreshStatus(String key);

	Connector getByKey(String key);

	void setStatus(String key, Status status);

}
