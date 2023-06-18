package com.duluthtechnologies.ocpi.core.service;

import java.util.List;
import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.model.v211.Credentials;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface RegisteredOperatorService {

	public static record RegisteredCPOCreationForm(

			String key,

			String versionUrl,

			String incomingToken,

			String outgoingToken,

			@NotEmpty String partyId,

			@NotEmpty String countryCode,

			String name,

			String logoUrl,

			String logoThumbnailUrl,

			String websiteUrl) {

		@AssertTrue
		private boolean validateTokenVersionUrl() {
			return (versionUrl != null && outgoingToken != null) || (versionUrl == null && outgoingToken == null);
		}

	}

	public static record RegisteredEMSPCreationForm(

			String key,

			String versionUrl,

			String incomingToken,

			String outgoingToken,

			@NotEmpty String partyId,

			@NotEmpty String countryCode,

			String name,

			String logoUrl,

			String logoThumbnailUrl,

			String websiteUrl) {

		@AssertTrue
		private boolean validateTokenApiUrl() {
			return (versionUrl != null && outgoingToken != null) || (versionUrl == null && outgoingToken == null);
		}

	}

	RegisteredCPO createRegisteredCPO(@Valid RegisteredCPOCreationForm registeredCPOCreationForm);

	RegisteredEMSP createRegisteredEMSP(@Valid RegisteredEMSPCreationForm registeredEMSPCreationForm);

	Optional<RegisteredOperator> findByIncomingToken(@NotEmpty String token);

	Optional<RegisteredCPO> findCPOByKey(String key);

	List<RegisteredCPO> findCPOs();

	List<RegisteredEMSP> findEMSPs();

	Optional<RegisteredEMSP> findEMSPByKey(String key);

	RegisteredCPO updateRegisteredCPO(@NotNull @Valid RegisteredCPO registeredCPO);

	RegisteredEMSP updateRegisteredEMSP(@NotNull @Valid RegisteredEMSP registeredEMSP);

	void performHandshakeWithCPO(String key);

	RegisteredCPO finalizeHandshakeWithCPO(String key, Credentials credentials);

	void performHandshakeWithEMSP(String key);

	RegisteredEMSP finalizeHandshakeWithEMSP(String emspKey, @Valid Credentials credentials);

}
