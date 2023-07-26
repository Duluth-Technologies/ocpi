package com.duluthtechnologies.ocpi.main.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;

@Configuration
@ConditionalOnProperty(prefix = "ocpi.cpo", name = "enabled", havingValue = "true")
public class CPOConfiguration implements CPOInfo {

	private OcpiApplicationProperties ocpiApplicationProperties;

	public CPOConfiguration(OcpiApplicationProperties ocpiApplicationProperties) {
		this.ocpiApplicationProperties = ocpiApplicationProperties;
	}

	@Override
	public String getCountryCode() {
		return ocpiApplicationProperties.getCpo().getCountryCode();
	}

	@Override
	public String getPartyId() {
		return ocpiApplicationProperties.getCpo().getPartyId();
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
