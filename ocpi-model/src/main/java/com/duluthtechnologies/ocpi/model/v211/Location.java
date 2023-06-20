package com.duluthtechnologies.ocpi.model.v211;

import java.time.Instant;
import java.util.List;

import com.duluthtechnologies.ocpi.model.TimestampDeserializer;
import com.duluthtechnologies.ocpi.model.TimestampSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Location(

		/**
		 * Uniquely identifies the location within the CPOs platform (and suboperator
		 * platforms). This field can never be changed, modified or renamed.
		 */
		@NotEmpty @Size(max = 39) String id,

		@NotNull Location.Type type,

		@Size(max = 255) String name,

		@NotEmpty @Size(max = 45) String address,

		@NotEmpty @Size(max = 45) String city,

		@JsonProperty("postal_code") @NotEmpty @Size(max = 10) String postalCode,

		/**
		 * ISO 3166-1 alpha-3 code for the country of this location.
		 */
		@NotEmpty @Size(min = 3, max = 3) String country,

		@NotNull @Valid GeoLocation coordinates,

		/**
		 * Geographical location of related points relevant to the user.
		 */
		@JsonProperty("related_locations") @Valid List<AdditionnalGeoLocation> relatedLocations,

		/**
		 * List of EVSEs that belong to this Location.
		 */
		@Valid List<EVSE> evses,

		/**
		 * Human-readable directions on how to reach the location.
		 */
		@Valid List<DisplayText> directions,

		/**
		 * Information of the operator. When not specified, the information retrieved
		 * from the api_info endpoint should be used instead.
		 */
		@Valid BusinessDetails operator,

		/**
		 * Information of the suboperator if available.
		 */
		@Valid BusinessDetails suboperator,

		/**
		 * Information of the owner if available.
		 */
		@Valid BusinessDetails owner,

		/**
		 * Optional list of facilities this charge location directly belongs to.
		 */
		List<Facility> facilities,

		/**
		 * One of IANA tzdata’s TZ-values representing the time zone of the location.
		 * Examples: “Europe/Oslo”, “Europe/Zurich”. (http://www.iana.org/time-zones)
		 */
		@Size(max = 255) String timeZone,

		/**
		 * The times when the EVSEs at the location can be accessed for charging.
		 */
		@JsonProperty("opening_times") @Valid Hours openingTimes,

		/**
		 * Indicates if the EVSEs are still charging outside the opening hours of the
		 * location. E.g. when the parking garage closes its barriers over night, is it
		 * allowed to charge till the next morning? Default: true
		 */
		@JsonProperty("charging_when_closed") boolean chargingWhenClosed,

		@Valid List<Image> images,

		@Valid @JsonProperty("energy_mix") EnergyMix energyMix,

		@NotNull @JsonDeserialize(using = TimestampDeserializer.class) @JsonSerialize(using = TimestampSerializer.class) @JsonProperty("last_updated") Instant lastUpdated) {

	public enum Type {

		/**
		 * Parking in public space.
		 */
		ON_STREET,

		/**
		 * Multistorey car park.
		 */
		PARKING_GARAGE,

		/**
		 * Multistorey car park, mainly underground.
		 */
		UNDERGROUND_GARAGE,

		/**
		 * A cleared area that is intended for parking vehicles, i.e. at super markets,
		 * bars, etc.
		 */
		PARKING_LOT,

		/**
		 * None of the given possibilities.
		 */
		OTHER,

		/**
		 * Parking location type is not known by the operator (default).
		 */
		UNKNOWN

	}

}
