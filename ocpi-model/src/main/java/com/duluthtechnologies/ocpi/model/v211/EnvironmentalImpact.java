package com.duluthtechnologies.ocpi.model.v211;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an environmental impact according to the OCPI 2.1.1 standard.
 */
public record EnvironmentalImpact(
		/**
		 * The category of this value.
		 */
		@NotNull EnvironmentalImpactCategory source,

		/**
		 * Amount of this portion in g/kWh. Numbers in OCPI use 4 decimals.
		 */
		@Digits(integer = 3, fraction = 4) double amount) {
}
