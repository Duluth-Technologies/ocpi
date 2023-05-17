package com.duluthtechnologies.ocpi.persistence.entity.v211;

import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "registered_emsps_v211")
public class RegisteredEMSPV211Entity extends RegisteredEMSPEntity implements RegisteredEMSPV211 {

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
