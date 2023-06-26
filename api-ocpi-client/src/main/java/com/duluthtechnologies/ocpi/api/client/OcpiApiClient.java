package com.duluthtechnologies.ocpi.api.client;

import java.util.List;

import com.duluthtechnologies.ocpi.model.v211.EVSE;

public interface OcpiApiClient {

	com.duluthtechnologies.ocpi.model.v211.EVSE getEvse211(String token, String locationUrl, String countryCode,
			String partyId, String locationId, String evseId) throws OcpiApiClientException;

	com.duluthtechnologies.ocpi.model.v211.Credentials postCredentialsV211(String token, String credentialsUrl,
			com.duluthtechnologies.ocpi.model.v211.Credentials credentials) throws OcpiApiClientException;

	void patchEvseV211(String token, String locationUrl, String countryCode, String partyId, String locationId,
			String evseId, EVSE evse) throws OcpiApiClientException;

	com.duluthtechnologies.ocpi.model.v211.Location putLocationV211(String token, String locationUrl,
			String countryCode, String partyId, String locationId,
			com.duluthtechnologies.ocpi.model.v211.Location location) throws OcpiApiClientException;

	List<com.duluthtechnologies.ocpi.model.v211.Location> getLocationsV211(String token, String locationUrl) throws OcpiApiClientException;


}
