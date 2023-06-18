package com.duluthtechnologies.ocpi.model.v211;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegularHours(

		/**
		 * Number of day in the week, from Monday (1) till Sunday (7)
		 */
		@Min(1) @Max(7) int weekday,

		/**
		 * Begin of the regular period given in hours and minutes. Must be in 24h format
		 * with leading zeros. Example: “18:15”. Hour/Minute separator: “:” Regex:
		 * [0-2][0-9]:[0-5][0-9]
		 */
		@Pattern(regexp = "[0-2][0-9]:[0-5][0-9]") @Size(max = 5) @NotEmpty @JsonProperty("period_begin") String periodBegin,

		/**
		 * End of the regular period, syntax as for period_begin. Must be later than
		 * period_begin.
		 */
		@Pattern(regexp = "[0-2][0-9]:[0-5][0-9]") @Size(max = 5) @NotEmpty @JsonProperty("period_end") String periodEnd) {

}
