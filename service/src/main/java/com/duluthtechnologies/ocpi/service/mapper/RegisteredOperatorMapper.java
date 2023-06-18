package com.duluthtechnologies.ocpi.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService.RegisteredCPOCreationForm;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService.RegisteredEMSPCreationForm;
import com.duluthtechnologies.ocpi.model.VersionNumber;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class RegisteredOperatorMapper {

	@Mapping(source = "key", target = "key")
	public abstract RegisteredCPOImpl toRegisteredCPO(String key, RegisteredCPOCreationForm registeredCPOCreationForm);

	@Mapping(source = "key", target = "key")
	public abstract RegisteredEMSPImpl toRegisteredEMSP(String key,
			RegisteredEMSPCreationForm registeredEMSPCreationForm);

	@Mapping(source = "incomingToken", target = "incomingToken")
	public abstract RegisteredCPOImpl updateIncomingToken(RegisteredCPO registeredCPO, String incomingToken);

	@Mapping(source = "incomingToken", target = "incomingToken")
	public abstract RegisteredEMSPImpl updateIncomingToken(RegisteredEMSP registeredEMSP, String incomingToken);

	@Mapping(source = "incomingToken", target = "incomingToken")
	@Mapping(source = "outgoingToken", target = "outgoingToken")
	public abstract RegisteredCPOV211Impl toRegisteredCPOV211(RegisteredCPO registeredCPO, String credentialsUrl,
			String locationsUrl, String incomingToken, String outgoingToken);

	@Mapping(source = "incomingToken", target = "incomingToken")
	@Mapping(source = "outgoingToken", target = "outgoingToken")
	public abstract RegisteredEMSPV211Impl toRegisteredEMSPV211(RegisteredEMSP registeredEMSP, String credentialsUrl,
			String locationsUrl, String incomingToken, String outgoingToken);

	protected static class RegisteredEMSPImpl implements RegisteredEMSP {

		String key;
		VersionNumber version;
		String versionUrl;
		String incomingToken;
		String outgoingToken;
		String partyId;
		String countryCode;
		String name;
		String logoUrl;
		String logoThumbnailUrl;
		String websiteUrl;

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getVersionUrl() {
			return versionUrl;
		}

		@Override
		public String getIncomingToken() {
			return incomingToken;
		}

		@Override
		public String getOutgoingToken() {
			return outgoingToken;
		}

		@Override
		public String getPartyId() {
			return partyId;
		}

		@Override
		public String getCountryCode() {
			return countryCode;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getLogoUrl() {
			return logoUrl;
		}

		@Override
		public String getLogoThumbnailUrl() {
			return logoThumbnailUrl;
		}

		@Override
		public String getWebsiteUrl() {
			return websiteUrl;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setVersionUrl(String versionUrl) {
			this.versionUrl = versionUrl;
		}

		public void setIncomingToken(String incomingToken) {
			this.incomingToken = incomingToken;
		}

		public void setOutgoingToken(String outgoingToken) {
			this.outgoingToken = outgoingToken;
		}

		public void setPartyId(String partyId) {
			this.partyId = partyId;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setLogoUrl(String logoUrl) {
			this.logoUrl = logoUrl;
		}

		public void setLogoThumbnailUrl(String logoThumbnailUrl) {
			this.logoThumbnailUrl = logoThumbnailUrl;
		}

		public void setWebsiteUrl(String websiteUrl) {
			this.websiteUrl = websiteUrl;
		}

	}

	protected static class RegisteredEMSPV211Impl extends RegisteredEMSPImpl implements RegisteredEMSPV211 {

		private String credentialsUrl;

		private String locationsUrl;

		@Override
		public String getCredentialsUrl() {
			return credentialsUrl;
		}

		public void setCredentialsUrl(String credentialsUrl) {
			this.credentialsUrl = credentialsUrl;
		}

		@Override
		public String getLocationsUrl() {
			return locationsUrl;
		}

		public void setLocationsUrl(String locationsUrl) {
			this.locationsUrl = locationsUrl;
		}

	}

	protected class RegisteredCPOImpl implements RegisteredCPO {

		String key;
		String versionUrl;
		String incomingToken;
		String outgoingToken;
		String partyId;
		String countryCode;
		String name;
		String logoUrl;
		String logoThumbnailUrl;
		String websiteUrl;

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getVersionUrl() {
			return versionUrl;
		}

		@Override
		public String getIncomingToken() {
			return incomingToken;
		}

		@Override
		public String getOutgoingToken() {
			return outgoingToken;
		}

		@Override
		public String getPartyId() {
			return partyId;
		}

		@Override
		public String getCountryCode() {
			return countryCode;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getLogoUrl() {
			return logoUrl;
		}

		@Override
		public String getLogoThumbnailUrl() {
			return logoThumbnailUrl;
		}

		@Override
		public String getWebsiteUrl() {
			return websiteUrl;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setVersionUrl(String versionUrl) {
			this.versionUrl = versionUrl;
		}

		public void setIncomingToken(String incomingToken) {
			this.incomingToken = incomingToken;
		}

		public void setOutgoingToken(String outgoingToken) {
			this.outgoingToken = outgoingToken;
		}

		public void setPartyId(String partyId) {
			this.partyId = partyId;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setLogoUrl(String logoUrl) {
			this.logoUrl = logoUrl;
		}

		public void setLogoThumbnailUrl(String logoThumbnailUrl) {
			this.logoThumbnailUrl = logoThumbnailUrl;
		}

		public void setWebsiteUrl(String websiteUrl) {
			this.websiteUrl = websiteUrl;
		}

	}

	protected class RegisteredCPOV211Impl extends RegisteredCPOImpl implements RegisteredCPOV211 {

		private String credentialsUrl;

		private String locationsUrl;

		@Override
		public String getCredentialsUrl() {
			return credentialsUrl;
		}

		public void setCredentialsUrl(String credentialsUrl) {
			this.credentialsUrl = credentialsUrl;
		}

		@Override
		public String getLocationsUrl() {
			return locationsUrl;
		}

		public void setLocationsUrl(String locationsUrl) {
			this.locationsUrl = locationsUrl;
		}

	}
}
