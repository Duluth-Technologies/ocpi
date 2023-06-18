package com.duluthtechnologies.ocpi.api.dto.v211;

import com.duluthtechnologies.ocpi.api.dto.RegisteredCPOView;

public class RegisteredCPOV211View extends RegisteredCPOView {

	private String credentialsUrl;

	private String locationsUrl;

	public String getCredentialsUrl() {
		return credentialsUrl;
	}

	public void setCredentialsUrl(String credentialsUrl) {
		this.credentialsUrl = credentialsUrl;
	}

	public String getLocationsUrl() {
		return locationsUrl;
	}

	public void setLocationsUrl(String locationsUrl) {
		this.locationsUrl = locationsUrl;
	}

}
