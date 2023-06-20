package com.duluthtechnologies.ocpi.model.v211;

import java.security.DrbgParameters.Capability;
import java.time.Instant;
import java.util.List;

import com.duluthtechnologies.ocpi.model.TimestampDeserializer;
import com.duluthtechnologies.ocpi.model.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents an Electric Vehicle Supply Equipment (EVSE) object according to
 * the OCPI 2.1.1 standard.
 */
public record EVSE(

		/**
		 * Uniquely identifies the EVSE within the CPOs platform (and suboperator
		 * platforms). This field can never be changed, modified or renamed.
		 */
		@NotNull @Size(max = 39) String uid,

		/**
		 * EVSE ID compliant with the “eMI3 standard version V1.0”.
		 */
		@JsonProperty("evse_id") @Size(max = 48) String evseId,

		/**
		 * Indicates the current status of the EVSE.
		 */
		@NotNull Status status,

		/**
		 * Indicates a planned status in the future of the EVSE.
		 */
		@JsonProperty("status_schedule") List<StatusSchedule> statusSchedule,

		/**
		 * List of functionalities that the EVSE is capable of.
		 */
		List<Capability> capabilities,

		/**
		 * List of available connectors on the EVSE.
		 */
		@NotEmpty List<Connector> connectors,

		/**
		 * Level on which the charging station is located.
		 */
		@JsonProperty("floor_level") @Size(max = 4) String floorLevel,

		/**
		 * Coordinates of the EVSE.
		 */
		GeoLocation coordinates,

		/**
		 * A number/string printed on the outside of the EVSE for visual identification.
		 */
		@JsonProperty("physical_reference") @Size(max = 16) String physicalReference,

		/**
		 * Multi-language human-readable directions when more detailed information on
		 * how to reach the EVSE from the Location is required.
		 */
		List<DisplayText> directions,

		/**
		 * The restrictions that apply to the parking spot.
		 */
		@JsonProperty("parking_restrictions") List<ParkingRestriction> parkingRestrictions,

		/**
		 * Links to images related to the EVSE such as photos or logos.
		 */
		List<Image> images,

		/**
		 * Timestamp when this EVSE or one of its Connectors was last updated (or
		 * created).
		 */
		@NotNull @JsonDeserialize(using = TimestampDeserializer.class) @JsonSerialize(using = TimestampSerializer.class) @JsonProperty("last_updated") Instant lastUpdated) {

}