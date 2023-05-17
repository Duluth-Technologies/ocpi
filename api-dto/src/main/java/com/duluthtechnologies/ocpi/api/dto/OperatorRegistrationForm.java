package com.duluthtechnologies.ocpi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

class OperatorRegistrationForm {

	@NotEmpty
	private String key;

	private String incomingToken;

	private String outgoingToken;

	@NotEmpty
	@Size(min = 3, max = 3)
	private String partyId;

	@Size(min = 2, max = 2)
	private String countryCode;

	@NotBlank
	private String name;

	private String versionUrl;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public String getVersionUrl() {
		return versionUrl;
	}

	public void setVersionUrl(String versionUrl) {
		this.versionUrl = versionUrl;
	}

}
