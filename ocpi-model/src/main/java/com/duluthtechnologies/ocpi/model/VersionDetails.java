package com.duluthtechnologies.ocpi.model;

import java.util.List;

public record VersionDetails(

		/**
		 * The version number.
		 */
		VersionNumber version,

		/**
		 * A list of supported endpoints for this version.
		 */
		List<Endpoint> endpoints) {

}
