package com.duluthtechnologies.ocpi.model.v211;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a Status Schedule according to the OCPI 2.1.1 standard.
 */
public record StatusSchedule(

		/**
		 * Begin of the scheduled period.
		 */
		@JsonProperty("period_begin") @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC") Instant periodBegin,

		/**
		 * End of the scheduled period, if known.
		 */
		@JsonProperty("period_end") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC") Instant periodEnd,

		/**
		 * Status value during the scheduled period.
		 */
		@NotNull Status status) {
}