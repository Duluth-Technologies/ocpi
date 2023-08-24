package com.duluthtechnologies.ocpi.api.client;

public interface OcpiApiV211Client {

	com.duluthtechnologies.ocpi.model.v211.Credentials postCredentialsV211(String token, String credentialsUrl,
			com.duluthtechnologies.ocpi.model.v211.Credentials credentials) throws OcpiApiClientException;

	com.duluthtechnologies.ocpi.model.v211.Credentials putCredentialsV211(String token, String credentialsUrl,
			com.duluthtechnologies.ocpi.model.v211.Credentials credentials) throws OcpiApiClientException;

}
