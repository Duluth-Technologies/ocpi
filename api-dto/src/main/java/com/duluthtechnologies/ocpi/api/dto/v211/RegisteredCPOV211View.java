package com.duluthtechnologies.ocpi.api.dto.v211;

import com.duluthtechnologies.ocpi.api.dto.RegisteredCPOView;

public class RegisteredCPOV211View extends RegisteredCPOView {

	private String credentialsUrl;

	public String getCredentialsUrl() {
		return credentialsUrl;
	}

	public void setCredentialsUrl(String credentialsUrl) {
		this.credentialsUrl = credentialsUrl;
	}

}
