package com.duluthtechnologies.ocpi.main.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.duluthtechnologies.ocpi.core.configuration.EMSPInfo;

@Configuration
@ConditionalOnProperty(prefix = "ocpi.emsp", name = "enabled", havingValue = "true")
public class EMSPConfiguration implements EMSPInfo {

	private OcpiApplicationProperties ocpiApplicationProperties;

	public EMSPConfiguration(OcpiApplicationProperties ocpiApplicationProperties) {
		this.ocpiApplicationProperties = ocpiApplicationProperties;
	}

	@Override
	public String getCountryCode() {
		return ocpiApplicationProperties.getEmsp().getCountryCode();
	}

	@Override
	public String getPartyId() {
		return ocpiApplicationProperties.getEmsp().getPartyId();
	}

	@Override
	public String getName() {
		return ocpiApplicationProperties.getName();
	}

	@Override
	public String getWebsiteUrl() {
		return ocpiApplicationProperties.getWebsiteUrl();
	}

	@Override
	public String getLogoUrl() {
		return ocpiApplicationProperties.getLogoUrl();
	}

}