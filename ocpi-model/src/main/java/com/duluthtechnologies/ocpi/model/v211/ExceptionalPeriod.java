package com.duluthtechnologies.ocpi.model.v211;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record ExceptionalPeriod(

		@NotNull @JsonProperty("period_begin") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC") Instant periodBegin,

		@NotNull @JsonProperty("period_end") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC") Instant periodEnd) {

}
