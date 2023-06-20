package com.duluthtechnologies.ocpi.model.v211;

import java.time.Instant;

import com.duluthtechnologies.ocpi.model.TimestampDeserializer;
import com.duluthtechnologies.ocpi.model.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a Status Schedule according to the OCPI 2.1.1 standard.
 */
public record StatusSchedule(

		/**
		 * Begin of the scheduled period.
		 */
		@JsonProperty("period_begin") @NotNull @JsonDeserialize(using = TimestampDeserializer.class) @JsonSerialize(using = TimestampSerializer.class) Instant periodBegin,

		/**
		 * End of the scheduled period, if known.
		 */
		@JsonProperty("period_end") @JsonDeserialize(using = TimestampDeserializer.class) @JsonSerialize(using = TimestampSerializer.class) Instant periodEnd,

		/**
		 * Status value during the scheduled period.
		 */
		@NotNull Status status) {
}