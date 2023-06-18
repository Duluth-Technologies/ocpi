package com.duluthtechnologies.ocpi.model.v211;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an energy source according to the OCPI 2.1.1 standard.
 */
public record EnergySource(
		/**
		 * The type of energy source.
		 */
		@NotNull @JsonProperty("source") EnergySourceCategory source,

		/**
		 * Percentage of this source (0-100) in the mix. Numbers in OCPI use 4 decimals.
		 */
		@NotNull @Digits(integer = 3, fraction = 4) @JsonProperty("percentage") double percentage) {
}
