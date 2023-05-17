package com.duluthtechnologies.ocpi.core.configuration;

import jakarta.validation.constraints.NotEmpty;

public interface OperatorInfo {
    
		@NotEmpty
        String getCountryCode();
    
		@NotEmpty
        String getPartyId();
        
		@NotEmpty
        String getName();

        String getWebsiteUrl();

        String getLogoUrl();
}
