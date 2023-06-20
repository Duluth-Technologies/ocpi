package com.duluthtechnologies.ocpi.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record Response<T>(
		/**
		 * Contains the actual response data object or list of objects from each
		 * request, depending on the cardinality of the response data, this is an array
		 * (card. * or +), or a single object (card. 1 or ?)
		 */
		T data,

		/**
		 * Response code, as listed in Status Codes, indicates how the request was
		 * handled. To avoid confusion with HTTP codes, at least four digits are used.
		 */
		@JsonProperty("status_code") int statusCode,

		/**
		 * An optional status message which may help when debugging.
		 */
		@JsonProperty("status_message") String statusMessage,

		/**
		 * The time this message was generated.
		 */
		@JsonDeserialize(using = TimestampDeserializer.class) @JsonSerialize(using = TimestampSerializer.class) Instant timestamp) {

}
