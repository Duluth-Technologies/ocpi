package com.duluthtechnologies.ocpi.api.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.api.client.OcpiApiClientException;
import com.duluthtechnologies.ocpi.api.client.OcpiApiV211Client;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.v211.Credentials;

public abstract class OcpiApiV211ClientImpl implements OcpiApiV211Client {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiApiV211ClientImpl.class);

	protected final RestTemplate restTemplate;

	protected OcpiApiV211ClientImpl() {
		super();
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
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
	public Credentials putCredentialsV211(String token, String credentialsUrl, Credentials credentials)
			throws OcpiApiClientException {
		LOG.debug("Putting Credentials on URL [{}]...", credentialsUrl);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Token " + token);
			HttpEntity entity = new HttpEntity<>(credentials, headers);
			Response<Credentials> response = restTemplate.exchange(credentialsUrl, HttpMethod.PUT, entity,
					new ParameterizedTypeReference<Response<Credentials>>() {
					}).getBody();

			if (response == null) {
				String message = "Null response returned when putting Credentials on URL [%s]."
						.formatted(credentialsUrl);
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
			if (response.statusCode() >= 2000) {
				String message = "Error occured when putting Credentials on URL [%s]. Status code: [%s]. Error message: [%s]"
						.formatted(credentialsUrl, Integer.toString(response.statusCode()), response.statusMessage());
				LOG.error(message);
				throw new OcpiApiClientException(message);
			}
			return response.data();
		} catch (OcpiApiClientException e) {
			throw e;
		} catch (Exception e) {
			String message = "Exception caught while putting Credentials on URL [%s].".formatted(credentialsUrl);
			LOG.error(message);
			throw new OcpiApiClientException(message);
		}
	}

}
