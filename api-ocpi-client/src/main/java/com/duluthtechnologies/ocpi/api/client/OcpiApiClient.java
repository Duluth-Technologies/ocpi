package com.duluthtechnologies.ocpi.api.client;

public interface OcpiApiClient {

	com.duluthtechnologies.ocpi.model.v211.EVSE getEvse211(String token, String locationUrl, String countryCode,
			String partyId, String locationId, String evseId) throws OcpiApiClientException;

	com.duluthtechnologies.ocpi.model.v211.Credentials postCredentialsV211(String token, String credentialsUrl,
			com.duluthtechnologies.ocpi.model.v211.Credentials credentials) throws OcpiApiClientException;

}
