package com.duluthtechnologies.ocpi.api.client;

import java.util.List;

public interface OcpiCPOApiV211Client extends OcpiApiV211Client {

	com.duluthtechnologies.ocpi.model.v211.EVSE getEvse211(String token, String locationUrl, String countryCode,
			String partyId, String locationId, String evseId) throws OcpiApiClientException;

	List<com.duluthtechnologies.ocpi.model.v211.Location> getLocationsV211(String token, String locationUrl)
			throws OcpiApiClientException;

}
