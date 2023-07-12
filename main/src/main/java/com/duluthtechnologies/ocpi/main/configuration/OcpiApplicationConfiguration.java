package com.duluthtechnologies.ocpi.main.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.configuration.EMSPInfo;
import com.duluthtechnologies.ocpi.main.configuration.OcpiApplicationProperties.Cpo;
import com.duluthtechnologies.ocpi.main.configuration.OcpiApplicationProperties.Emsp;

// We want to make sure this Configuration is applied before the Controllers, as some depend on the existence of EMSPInfo and CPOInfo beans
@AutoConfigureBefore(value = WebMvcAutoConfiguration.class)
@Configuration
public class OcpiApplicationConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiApplicationConfiguration.class);

	@Bean
	@ConditionalOnProperty(prefix = "ocpi.cpo", name = "enabled", havingValue = "true")
	public CPOInfo cpoInfo(OcpiApplicationProperties ocpiApplicationProperties) {
		Cpo cpo = ocpiApplicationProperties.getCpo();
		LOG.info("CPO module is enabled with country code [{}] and party id [{}].", cpo.getCountryCode(),
				cpo.getPartyId());
		return new CPOInfoImpl(cpo.getCountryCode(), cpo.getPartyId(), ocpiApplicationProperties.getName(),
				ocpiApplicationProperties.getWebsiteUrl(), ocpiApplicationProperties.getLogoUrl());
	}

	@Bean
	@ConditionalOnProperty(prefix = "ocpi.emsp", name = "enabled", havingValue = "true")
	public EMSPInfo emspInfo(OcpiApplicationProperties ocpiApplicationProperties) {
		Emsp emsp = ocpiApplicationProperties.getEmsp();
		LOG.info("EMSP module is enabled with country code [{}] and party id [{}].", emsp.getCountryCode(),
				emsp.getPartyId());
		return new EMSPInfoImpl(emsp.getCountryCode(), emsp.getPartyId(), ocpiApplicationProperties.getName(),
				ocpiApplicationProperties.getWebsiteUrl(), ocpiApplicationProperties.getLogoUrl());
	}

	private static record CPOInfoImpl(String countryCode, String partyId, String name, String websiteUrl,
			String logoUrl) implements CPOInfo {

		@Override
		public String getCountryCode() {
			return countryCode;
		}

		@Override
		public String getPartyId() {
			return partyId;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getWebsiteUrl() {
			return websiteUrl;
		}

		@Override
		public String getLogoUrl() {
			return logoUrl;
		}

	}

	private static record EMSPInfoImpl(String countryCode, String partyId, String name, String websiteUrl,
			String logoUrl) implements EMSPInfo {

		@Override
		public String getCountryCode() {
			return countryCode;
		}

		@Override
		public String getPartyId() {
			return partyId;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getWebsiteUrl() {
			return websiteUrl;
		}

		@Override
		public String getLogoUrl() {
			return logoUrl;
		}

	}

}
