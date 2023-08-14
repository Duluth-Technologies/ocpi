package com.duluthtechnologies.ocpi.api.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.duluthtechnologies.ocpi.api.client.OcpiApiClientException;
import com.duluthtechnologies.ocpi.api.client.OcpiEMSPApiV211Client;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Location;
import com.duluthtechnologies.ocpi.model.v211.Session;

@Service
public class OcpiEMSPApiV211ClientImpl extends OcpiApiV211ClientImpl implements OcpiEMSPApiV211Client {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiEMSPApiV211ClientImpl.class);

	public OcpiEMSPApiV211ClientImpl() {
		super();
	}

	@Override
	public void patchEvseV211(String token, String locationUrl, String countryCode, String partyId, String locationId,
			String evseId, EVSE evse) throws OcpiApiClientException {
		LOG.debug(
				"Patching Evse v2.1.1 on location URL [{}] for country code [{}] and party id [{}] and location id [{}] and evse id [{}]...",
				locationUrl, countryCode, partyId, locationId, evseId);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Token " + token);
			HttpEntity entity = new HttpEntity<>(evse, headers);
			Response response = restTemplate
					.exchange(locationUrl + "/" + countryCode + "/" + partyId + "/" + locationId + "/" + evseId,
							HttpMethod.PATCH, entity, Response.class)
					.getBody();
			if (response == null) {
				String message = "Null response returned when patching Evse v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and location id [%s] and evse id [%s]."
						.formatted(locationUrl, countryCode, partyId, locationId, evseId);
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
			if (response.statusCode() >= 2000) {
				String message = "Error occured when patching Evse v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and location id [%s] and evse id [%s]. Status code: [%s]. Error message: [%s]"
						.formatted(locationUrl, countryCode, partyId, locationId, evseId,
								Integer.toString(response.statusCode()), response.statusMessage());
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
		} catch (OcpiApiClientException e) {
			throw e;
		} catch (Exception e) {
			String message = "Exception caught while patching Evse v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and location id [%s] and evse id [%s]."
					.formatted(locationUrl, countryCode, partyId, locationId, evseId);
			LOG.error(message);
			throw new RuntimeException(message, e);
		}
	}

	@Override
	public Location putLocationV211(String token, String locationUrl, String countryCode, String partyId,
			String locationId, Location location) throws OcpiApiClientException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity entity = new HttpEntity<>(location, headers);
		Response<com.duluthtechnologies.ocpi.model.v211.Location> response = restTemplate
				.exchange(locationUrl + "/" + countryCode + "/" + partyId + "/" + locationId, HttpMethod.PUT, entity,
						new ParameterizedTypeReference<Response<com.duluthtechnologies.ocpi.model.v211.Location>>() {
						})
				.getBody();
		if (response == null) {
			String message = "Null response returned when putting Location v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and location id [%s]."
					.formatted(locationUrl, countryCode, partyId, locationId);
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
		if (response.statusCode() >= 2000) {
			String message = "Error occured when putting Location v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and location id [%s]. Status code: [%s]. Error message: [%s]"
					.formatted(locationUrl, countryCode, partyId, locationId, Integer.toString(response.statusCode()),
							response.statusMessage());
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
		return response.data();
	}

	@Override
	public Session putSessionV211(String token, String sessionUrl, String countryCode, String partyId, String sessionId,
			com.duluthtechnologies.ocpi.model.v211.Session session) throws OcpiApiClientException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity entity = new HttpEntity<>(session, headers);
		Response<com.duluthtechnologies.ocpi.model.v211.Session> response = restTemplate
				.exchange(sessionUrl + "/" + countryCode + "/" + partyId + "/" + sessionId, HttpMethod.PUT, entity,
						new ParameterizedTypeReference<Response<com.duluthtechnologies.ocpi.model.v211.Session>>() {
						})
				.getBody();
		if (response == null) {
			String message = "Null response returned when putting Session v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and session id [%s]."
					.formatted(sessionUrl, countryCode, partyId, sessionId);
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
		if (response.statusCode() >= 2000) {
			String message = "Error occured when putting Session v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and session id [%s]. Status code: [%s]. Error message: [%s]"
					.formatted(sessionUrl, countryCode, partyId, sessionId, Integer.toString(response.statusCode()),
							response.statusMessage());
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
		return response.data();
	}

}
