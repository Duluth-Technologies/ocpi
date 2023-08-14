package com.duluthtechnologies.ocpi.model.v211;

/**
 * AuthMethod enum defines the authentication method used for a session.
 */
public enum AuthMethod {
	/**
	 * Authentication request from the eMSP.
	 */
	AUTH_REQUEST,

	/**
	 * Whitelist used to authenticate, no request done to the eMSP.
	 */
	WHITELIST
}