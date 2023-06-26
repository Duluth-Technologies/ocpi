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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.api.client.OcpiApiClient;
import com.duluthtechnologies.ocpi.api.client.OcpiApiClientException;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.Credentials;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Location;

@Service
public class OcpiApiClientImpl implements OcpiApiClient {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiApiClientImpl.class);

	private final RestTemplate restTemplate;

	public OcpiApiClientImpl() {
		super();
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
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
	public Credentials postCredentialsV211(String token, String credentialsUrl, Credentials credentials)
			throws OcpiApiClientException {
		LOG.debug("Posting Credentials on URL [{}]...", credentialsUrl);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Token " + token);
			HttpEntity entity = new HttpEntity<>(credentials, headers);
			Response<Credentials> response = restTemplate.exchange(credentialsUrl, HttpMethod.POST, entity,
					new ParameterizedTypeReference<Response<Credentials>>() {
					}).getBody();

			if (response == null) {
				String message = "Null response returned when posting Credentials on URL [%s]."
						.formatted(credentialsUrl);
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
			if (response.statusCode() >= 2000) {
				String message = "Error occured when posting Credentials on URL [%s]. Status code: [%s]. Error message: [%s]"
						.formatted(credentialsUrl, Integer.toString(response.statusCode()), response.statusMessage());
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
			return response.data();
		} catch (OcpiApiClientException e) {
			throw e;
		} catch (Exception e) {
			String message = "Exception caught while posting Credentials on URL [%s].".formatted(credentialsUrl);
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
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
				String message = "Error occured when when patching Evse v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and location id [%s] and evse id [%s]. Status code: [%s]. Error message: [%s]"
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
			String message = "Error occured when when putting Location v2.1.1 on location URL [%s] for country code [%s] and party id [%s] and location id [%s]. Status code: [%s]. Error message: [%s]"
					.formatted(locationUrl, countryCode, partyId, locationId, Integer.toString(response.statusCode()),
							response.statusMessage());
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
		return response.data();
	}

	@Override
	public List<Location> getLocationsV211(String token, String locationUrl)
			throws OcpiApiClientException {
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
