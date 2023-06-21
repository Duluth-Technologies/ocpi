package com.duluthtechnologies.ocpi.api.client;

public class OcpiApiClientException extends Exception {

	private static final long serialVersionUID = -7621554983870359483L;

	public OcpiApiClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public OcpiApiClientException(String message) {
		super(message);
	}

}
