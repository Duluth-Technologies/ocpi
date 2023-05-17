package com.duluthtechnologies.ocpi.api.dto;

import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredCPOV211View;
import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredEMSPV211View;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = RegisteredCPOV211View.class, name = "cpo_v211"),
		@Type(value = RegisteredCPOView.class, name = "cpo"),
		@Type(value = RegisteredEMSPV211View.class, name = "emsp_v211"),
		@Type(value = RegisteredEMSPView.class, name = "emsp") })
abstract class RegisteredOperatorView {

	private String key;

	private String versionUrl;

	private String incomingToken;

	private String outgoingToken;

	private String partyId;

	private String countryCode;

	private String name;

	private String logoUrl;

	private String logoThumbnailUrl;

	private String websiteUrl;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVersionUrl() {
		return versionUrl;
	}

	public void setVersionUrl(String versionUrl) {
		this.versionUrl = versionUrl;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getLogoThumbnailUrl() {
		return logoThumbnailUrl;
	}

	public void setLogoThumbnailUrl(String logoThumbnailUrl) {
		this.logoThumbnailUrl = logoThumbnailUrl;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getIncomingToken() {
		return incomingToken;
	}

	public void setIncomingToken(String incomingToken) {
		this.incomingToken = incomingToken;
	}

	public String getOutgoingToken() {
		return outgoingToken;
	}

	public void setOutgoingToken(String outgoingToken) {
		this.outgoingToken = outgoingToken;
	}

}
