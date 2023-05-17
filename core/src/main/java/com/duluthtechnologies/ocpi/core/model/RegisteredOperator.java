package com.duluthtechnologies.ocpi.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public interface RegisteredOperator {

	@NotEmpty
	String getKey();

	String getVersionUrl();

	String getIncomingToken();

	String getOutgoingToken();

	@Size(min = 3, max = 3)
	String getPartyId();

	@Size(min = 2, max = 2)
	String getCountryCode();

	@NotEmpty
	String getName();

	String getLogoUrl();

	String getLogoThumbnailUrl();

	String getWebsiteUrl();

}
