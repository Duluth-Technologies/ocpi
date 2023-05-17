package com.duluthtechnologies.ocpi.model;

public record Endpoint(

		/**
		 * Endpoint identifier.
		 */
		ModuleID identifier,

		/**
		 * URL to the endpoint.
		 */
		String url) {

}
