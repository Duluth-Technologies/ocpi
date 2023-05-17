package com.duluthtechnologies.ocpi.main.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ocpi")
public class OcpiApplicationProperties {

	public static class Cpo {

		private String countryCode;

		private String partyId;

		private boolean enabled = false;

		public String getCountryCode() {
			return countryCode;
		}

		public String getPartyId() {
			return partyId;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public void setPartyId(String partyId) {
			this.partyId = partyId;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

	public static class Emsp {

		private String countryCode;

		private String partyId;

		private boolean enabled = false;

		public String getCountryCode() {
			return countryCode;
		}

		public String getPartyId() {
			return partyId;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public void setPartyId(String partyId) {
			this.partyId = partyId;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

	private Cpo cpo;

	private Emsp emsp;

	private String logoUrl;

	private String name;

	private String websiteUrl;

	public Cpo getCpo() {
		return cpo;
	}

	public Emsp getEmsp() {
		return emsp;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public String getName() {
		return name;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setCpo(Cpo cpo) {
		this.cpo = cpo;
	}

	public void setEmsp(Emsp emsp) {
		this.emsp = emsp;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

}
