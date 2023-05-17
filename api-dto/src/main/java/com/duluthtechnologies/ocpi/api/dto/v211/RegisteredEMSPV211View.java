package com.duluthtechnologies.ocpi.api.dto.v211;

import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPView;

public class RegisteredEMSPV211View extends RegisteredEMSPView {

	private String credentialsUrl;

	public String getCredentialsUrl() {
		return credentialsUrl;
	}

	public void setCredentialsUrl(String credentialsUrl) {
		this.credentialsUrl = credentialsUrl;
	}

}
