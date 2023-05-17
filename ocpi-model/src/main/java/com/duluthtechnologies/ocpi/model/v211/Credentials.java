package com.duluthtechnologies.ocpi.model.v211;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Credentials(

		/**
		 * The token for the other party to authenticate in your system.
		 */
		@JsonProperty("token") @NotEmpty @Size(max = 64) String token,

		/**
		 * The URL to your API versions endpoint.
		 */
		@JsonProperty("url") @NotEmpty String versionUrl,

		/**
		 * Details of this party.
		 */
		@JsonProperty("business_details") @NotNull @Valid BusinessDetails businessDetails,

		/**
		 * CPO or eMSP ID of this party. (following the 15118 ISO standard).
		 */
		@JsonProperty("party_id") @NotNull @Size(min = 3, max = 3) String partyId,

		/**
		 * Country code of the country this party is operating in.
		 */
		@JsonProperty("country_code") @NotNull @Size(min = 2, max = 2) String countryCode

) {

}
