package com.duluthtechnologies.ocpi.api.client;

import com.duluthtechnologies.ocpi.model.v211.EVSE;

public interface OcpiEMSPApiV211Client extends OcpiApiV211Client {

	void patchEvseV211(String token, String locationUrl, String countryCode, String partyId, String locationId,
			String evseId, EVSE evse) throws OcpiApiClientException;

	com.duluthtechnologies.ocpi.model.v211.Location putLocationV211(String token, String locationUrl,
			String countryCode, String partyId, String locationId,
			com.duluthtechnologies.ocpi.model.v211.Location location) throws OcpiApiClientException;

	com.duluthtechnologies.ocpi.model.v211.Session putSessionV211(String token, String sessionUrl, String countryCode,
			String partyId, String sessionId, com.duluthtechnologies.ocpi.model.v211.Session session)
			throws OcpiApiClientException;

}
