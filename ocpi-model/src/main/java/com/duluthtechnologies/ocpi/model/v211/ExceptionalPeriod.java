package com.duluthtechnologies.ocpi.model.v211;

import java.time.Instant;

import com.duluthtechnologies.ocpi.model.TimestampDeserializer;
import com.duluthtechnologies.ocpi.model.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotNull;

public record ExceptionalPeriod(

		@NotNull @JsonProperty("period_begin") @JsonDeserialize(using = TimestampDeserializer.class) @JsonSerialize(using = TimestampSerializer.class) Instant periodBegin,

		@NotNull @JsonProperty("period_end") @JsonDeserialize(using = TimestampDeserializer.class) @JsonSerialize(using = TimestampSerializer.class) Instant periodEnd) {

}
