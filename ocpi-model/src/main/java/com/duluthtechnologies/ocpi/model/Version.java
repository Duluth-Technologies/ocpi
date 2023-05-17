package com.duluthtechnologies.ocpi.model;

public record Version(

		/**
		 * The version number.
		 */
		VersionNumber version,

		/**
		 * URL to the endpoint containing version specific information.
		 */
		String url) {

}
