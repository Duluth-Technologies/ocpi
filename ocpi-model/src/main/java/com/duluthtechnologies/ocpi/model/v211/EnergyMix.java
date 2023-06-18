package com.duluthtechnologies.ocpi.model.v211;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents energy mix according to the OCPI 2.1.1 standard.
 */
public record EnergyMix(
		/**
		 * True if 100% from regenerative sources. (CO2 and nuclear waste is zero)
		 */
		@NotNull @JsonProperty("is_green_energy") boolean isGreenEnergy,

		/**
		 * Key-value pairs (enum + percentage) of energy sources of this location’s
		 * tariff.
		 */
		@JsonProperty("energy_sources") List<EnergySource> energySources,

		/**
		 * Key-value pairs (enum + percentage) of nuclear waste and CO2 exhaust of this
		 * location’s tariff.
		 */
		@JsonProperty("environ_impact") List<EnvironmentalImpact> environmentalImpacts,

		/**
		 * Name of the energy supplier, delivering the energy for this location or
		 * tariff.
		 */
		@Size(max = 64) @JsonProperty("supplier_name") String supplierName,

		/**
		 * Name of the energy suppliers product/tariff plan used at this location.
		 */
		@Size(max = 64) @JsonProperty("energy_product_name") String energyProductName) {
}
