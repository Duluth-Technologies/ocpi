package com.duluthtechnologies.ocpi.persistence.entity.v211;

import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredCPOEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "registered_cpos_v211")
public class RegisteredCPOV211Entity extends RegisteredCPOEntity implements RegisteredCPOV211 {

	@Column(name = "credentials_url")
	private String credentialsUrl;

	@Override
	public String getCredentialsUrl() {
		return credentialsUrl;
	}

	public void setCredentialsUrl(String credentialsUrl) {
		this.credentialsUrl = credentialsUrl;
	}

}
