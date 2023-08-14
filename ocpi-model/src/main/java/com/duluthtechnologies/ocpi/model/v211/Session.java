package com.duluthtechnologies.ocpi.model.v211;

import java.time.LocalDateTime;
import java.util.List;

import com.duluthtechnologies.ocpi.model.v211.Session.SessionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Session record represents an active or completed charging session.
 */
public record Session(
		/**
		 * The ID of the session.
		 */
		String id,

		/**
		 * The time when the session became active.
		 */
		@JsonProperty("start_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,

		/**
		 * The time when the session is completed.
		 */
		@JsonProperty("end_datetime") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,

		/**
		 * How many kWh are charged.
		 */
		Double kwh,

		/**
		 * Reference to a token, identified by the auth_id field of the Token.
		 */
		@JsonProperty("auth_id") String authId,

		/**
		 * Method used for authentication.
		 */
		@JsonProperty("auth_method") AuthMethod authMethod,

		/**
		 * Location of the session.
		 */
		Location location,

		/**
		 * Optional identification of the kWh meter.
		 */
		@JsonProperty("meter_id") String meterId,

		/**
		 * ISO 4217 code of the currency used for this session.
		 */
		String currency,

		/**
		 * An optional list of charging periods that can be used to calculate and verify
		 * the total cost.
		 */
		@JsonProperty("charging_periods") List<ChargingPeriod> chargingPeriods,

		/**
		 * The total cost (excluding VAT) of the session in the specified currency. This
		 * is the price that the eMSP will have to pay to the CPO. A total_cost of 0.00
		 * means free of charge. When omitted, no price information is given in the
		 * Session object, this does not have to mean it is free of charge.
		 */
		@JsonProperty("total_cost") Double totalCost,

		/**
		 * The status of the session.
		 */
		SessionStatus status,

		/**
		 * Timestamp when this Session was last updated (or created).
		 */
		@JsonProperty("last_updated") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastUpdated) {

	/**
	 * SessionStatus enum defines the state of a session.
	 */
	public enum SessionStatus {
		/**
		 * The session is accepted and active. All pre-conditions are met: Communication
		 * between EV and EVSE (for example: cable plugged in correctly), EV or Driver
		 * is authorized. EV is being charged, or can be charged. Energy is, or is not,
		 * being transferred.
		 */
		ACTIVE,

		/**
		 * The session is finished successfully. No more modifications will be made to
		 * this session.
		 */
		COMPLETED,

		/**
		 * The session is declared invalid and will not be billed.
		 */
		INVALID,

		/**
		 * The session is pending, it has not yet started. Not all pre-conditions are
		 * met. This is the initial state. This session might never become an active
		 * session.
		 */
		PENDING
	}

}