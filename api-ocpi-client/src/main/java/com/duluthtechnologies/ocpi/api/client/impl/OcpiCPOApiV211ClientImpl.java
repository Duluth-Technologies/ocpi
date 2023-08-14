package com.duluthtechnologies.ocpi.api.client.impl;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.duluthtechnologies.ocpi.api.client.OcpiApiClientException;
import com.duluthtechnologies.ocpi.api.client.OcpiCPOApiV211Client;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Location;

@Service
public class OcpiCPOApiV211ClientImpl extends OcpiApiV211ClientImpl implements OcpiCPOApiV211Client {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiCPOApiV211ClientImpl.class);

	public OcpiCPOApiV211ClientImpl() {
		super();
	}

	@Override
	public EVSE getEvse211(String token, String locationUrl, String countryCode, String partyId, String locationId,
			String evseId) throws OcpiApiClientException {
		LOG.debug(
				"Getting Evse v2.1.1 with id [{}] and location id [{}] for CPO with country code [{}] and partyId [{}] on URL [{}]...",
				evseId, locationId, countryCode, partyId, locationUrl);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Token " + token);
			HttpEntity entity = new HttpEntity<>(headers);
			Response<com.duluthtechnologies.ocpi.model.v211.EVSE> response = restTemplate.exchange(
					locationUrl + "/" + countryCode + "/" + partyId + "/" + locationId + "/" + evseId, HttpMethod.GET,
					entity, new ParameterizedTypeReference<Response<com.duluthtechnologies.ocpi.model.v211.EVSE>>() {
					}).getBody();
			if (response == null) {
				String message = "Null response returned when getting Evse v2.1.1 with id [%s] and location id [%s] for CPO with country code [%s] and partyId [%s] on URL [%s]. Status code: [%s]. Error message: [%s]"
						.formatted(evseId, locationId, countryCode, partyId, locationUrl,
								Integer.toString(response.statusCode()), response.statusMessage());
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
			if (response.statusCode() >= 2000) {
				String message = "Error occured when getting Evse v2.1.1 with id [%s] and location id [%s] for CPO with country code [%s] and partyId [%s] on URL [%s]. Status code: [%s]. Error message: [%s]"
						.formatted(evseId, locationId, countryCode, partyId, locationUrl,
								Integer.toString(response.statusCode()), response.statusMessage());
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
			return response.data();
		} catch (OcpiApiClientException e) {
			throw e;
		} catch (Exception e) {
			String message = "Exception caught while getting Evse v2.1.1 with id [%s] and location id [%s] for CPO with country code [%s] and partyId [%s] on URL [%s]."
					.formatted(evseId, locationId, countryCode, partyId, locationId);
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
	}

	@Override
	public List<Location> getLocationsV211(String token, String locationUrl) throws OcpiApiClientException {
		List<com.duluthtechnologies.ocpi.model.v211.Location> locationV211s = new LinkedList<>();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity entity = new HttpEntity<>(headers);
		ResponseEntity<Response<List<com.duluthtechnologies.ocpi.model.v211.Location>>> responseEntity = restTemplate
				.exchange(locationUrl, HttpMethod.GET, entity,
						new ParameterizedTypeReference<Response<List<com.duluthtechnologies.ocpi.model.v211.Location>>>() {
						});
		locationV211s.addAll(responseEntity.getBody().data());
		List<String> linkHeaders = responseEntity.getHeaders().get("Link");
		while (linkHeaders != null && !linkHeaders.isEmpty()) {
			responseEntity = restTemplate.exchange(linkHeaders.get(0), HttpMethod.GET, entity,
					new ParameterizedTypeReference<Response<List<com.duluthtechnologies.ocpi.model.v211.Location>>>() {
					});
			locationV211s.addAll(responseEntity.getBody().data());
			linkHeaders = responseEntity.getHeaders().get("Link");
		}
		return locationV211s;
	}

}
