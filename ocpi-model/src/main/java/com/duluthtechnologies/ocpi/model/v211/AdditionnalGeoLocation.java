package com.duluthtechnologies.ocpi.model.v211;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdditionnalGeoLocation(

		/**
		 * Latitude of the point in decimal degree. Example: 50.770774. Decimal
		 * separator: “.” Regex: -?[0-9]{1,2}\.[0-9]{6}
		 */
		@Pattern(regexp = "-?[0-9]{1,2}\\.[0-9]{6}") @NotNull String latitude,

		/**
		 * Longitude of the point in decimal degree. Example: -126.104965. Decimal
		 * separator: “.” Regex: -?[0-9]{1,3}\.[0-9]{6}
		 */
		@Pattern(regexp = "-?[0-9]{1,3}\\.[0-9]{6}") @NotNull String longitude,

		/**
		 * Name of the point in local language or as written at the location. For
		 * example the street name of a parking lot entrance or it’s number.
		 */
		@Valid DisplayText name) {

}
