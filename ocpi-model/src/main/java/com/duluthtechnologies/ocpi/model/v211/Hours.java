package com.duluthtechnologies.ocpi.model.v211;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public record Hours(

		@Valid RegularHours regularHours,

		@JsonProperty("twentyfourseven") boolean twentyFourSeven,

		@JsonProperty("exceptional_openings") @Valid List<ExceptionalPeriod> exceptionalOpenings,

		@JsonProperty("exceptional_closings") @Valid List<ExceptionalPeriod> exceptionalClosings) {

}
