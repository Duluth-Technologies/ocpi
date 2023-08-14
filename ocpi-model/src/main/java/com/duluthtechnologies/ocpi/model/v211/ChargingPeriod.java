package com.duluthtechnologies.ocpi.model.v211;

import java.time.LocalDateTime;
import java.util.List;

import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod.CdrDimension;
import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod.CdrDimension.CdrDimensionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A charging period consists of a start timestamp and a list of possible values
 * that influence this period, for example: Amount of energy charged this
 * period, maximum current during this period etc.
 */
public record ChargingPeriod(
		/**
		 * Start timestamp of the charging period. This period ends when a next period
		 * starts, the last period ends when the session ends.
		 */
		@JsonProperty("start_date_time") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,

		/**
		 * List of relevant values for this charging period.
		 */
		@Size(min = 1, message = "At least one CdrDimension is required.") List<CdrDimension> dimensions) {

	public record CdrDimension(
			/**
			 * Type of CDR dimension.
			 */
			@NotNull(message = "CdrDimensionType cannot be null.") CdrDimensionType type,

			/**
			 * Volume of the dimension consumed, measured according to the dimension type.
			 */
			@NotNull(message = "Volume cannot be null.") Double volume) {

		public enum CdrDimensionType {
			/**
			 * Defined in kWh, default step_size is 1 Wh.
			 */
			ENERGY,

			/**
			 * Flat fee, no unit.
			 */
			FLAT,

			/**
			 * Defined in A (Ampere), Maximum current reached during charging session.
			 */
			MAX_CURRENT,

			/**
			 * Defined in A (Ampere), Minimum current used during charging session.
			 */
			MIN_CURRENT,

			/**
			 * Time not charging: defined in hours, default step_size is 1 second.
			 */
			PARKING_TIME,

			/**
			 * Time charging: defined in hours, default step_size is 1 second.
			 */
			TIME
		}
	}

}
