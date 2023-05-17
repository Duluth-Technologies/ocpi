package com.duluthtechnologies.ocpi.service.impl;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.configuration.EMSPInfo;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.model.Endpoint;
import com.duluthtechnologies.ocpi.model.ModuleID;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.model.Version;
import com.duluthtechnologies.ocpi.model.VersionDetails;
import com.duluthtechnologies.ocpi.model.VersionNumber;
import com.duluthtechnologies.ocpi.model.v211.BusinessDetails;
import com.duluthtechnologies.ocpi.model.v211.Credentials;
import com.duluthtechnologies.ocpi.model.v211.Image;
import com.duluthtechnologies.ocpi.service.helper.KeyGenerator;
import com.duluthtechnologies.ocpi.service.mapper.RegisteredOperatorMapper;

import jakarta.validation.Valid;

@Component
public class RegisteredOperatorServiceImpl implements RegisteredOperatorService {

	private static final Logger LOG = LoggerFactory.getLogger(RegisteredOperatorServiceImpl.class);

	private final RegisteredOperatorStore registeredOperatorStore;

	private final RegisteredOperatorMapper registeredOperatorMapper;

	private final Optional<CPOInfo> cpoInfo;

	private final Optional<EMSPInfo> emspInfo;

	private final String externalOcpiApiUrl;

	private final RestTemplate restTemplate;

	public RegisteredOperatorServiceImpl(RegisteredOperatorStore registeredOperatorStore,
			RegisteredOperatorMapper registeredOperatorMapper, Optional<CPOInfo> cpoInfo,
			@Qualifier("externalOcpiApiUrl") String externalOcpiApiUrl, Optional<EMSPInfo> emspInfo) {
		super();
		this.registeredOperatorStore = registeredOperatorStore;
		this.registeredOperatorMapper = registeredOperatorMapper;
		this.cpoInfo = cpoInfo;
		this.emspInfo = emspInfo;
		this.externalOcpiApiUrl = externalOcpiApiUrl;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
	}

	@Override
	public RegisteredCPO createRegisteredCPO(RegisteredCPOCreationForm registeredCPOCreationForm) {
		// Generate key if no key is provided
		String key = registeredCPOCreationForm.key() != null ? registeredCPOCreationForm.key()
				: KeyGenerator.generateKey(RegisteredCPO.class);
		RegisteredCPO registeredCPO = registeredOperatorMapper.toRegisteredCPO(key, registeredCPOCreationForm);
		return (RegisteredCPO) registeredOperatorStore.create(registeredCPO);
	}

	@Override
	public RegisteredEMSP createRegisteredEMSP(RegisteredEMSPCreationForm registeredEMSPCreationForm) {
		// Generate key if no key is provided
		String key = registeredEMSPCreationForm.key() != null ? registeredEMSPCreationForm.key()
				: KeyGenerator.generateKey(RegisteredEMSP.class);
		RegisteredEMSP registeredEMSP = registeredOperatorMapper.toRegisteredEMSP(key, registeredEMSPCreationForm);
		return (RegisteredEMSP) registeredOperatorStore.create(registeredEMSP);
	}

	@Override
	public Optional<RegisteredOperator> findByIncomingToken(String token) {
		return registeredOperatorStore.findByIncomingToken(token);
	}

	@Override
	public Optional<RegisteredCPO> findCPOByKey(String key) {
		return registeredOperatorStore.findByKey(key).map(RegisteredCPO.class::cast);
	}

	@Override
	public Optional<RegisteredEMSP> findEMSPByKey(String key) {
		return registeredOperatorStore.findByKey(key).map(RegisteredEMSP.class::cast);
	}

	@Override
	public RegisteredCPO updateRegisteredCPO(RegisteredCPO registeredCPO) {
		LOG.debug("Updating Registered CPO of type [{}] with key [{}]...", registeredCPO.getClass().getCanonicalName(),
				registeredCPO.getKey());
		return (RegisteredCPO) registeredOperatorStore.update(registeredCPO);
	}

	@Override
	public RegisteredEMSP updateRegisteredEMSP(RegisteredEMSP registeredEMSP) {
		LOG.debug("Updating Registered EMSP of type [{}] with key [{}]...",
				registeredEMSP.getClass().getCanonicalName(), registeredEMSP.getKey());
		return (RegisteredEMSP) registeredOperatorStore.update(registeredEMSP);
	}

//	private RegisteredEMSP updateRegisteredEMSPNow(RegisteredEMSP registeredEMSP) {
//		LOG.debug("Updating now Registered EMSP of type [{}] with key [{}]...",
//				registeredEMSP.getClass().getCanonicalName(), registeredEMSP.getKey());
//		return (RegisteredEMSP) registeredOperatorStore.updateNow(registeredEMSP);
//	}

	@Override
	public void performHandshakeWithCPO(String key) {
		RegisteredCPO registeredCPO = (RegisteredCPO) registeredOperatorStore.findByKey(key).orElseThrow(() -> {
			String message = "Cannot perform handshake with CPO with key [%s] as it cannot be found.".formatted(key);
			LOG.error(message);
			return new RuntimeException(message); // TODO
		});
		String token = registeredCPO.getOutgoingToken();
		if (token == null) {
			String message = "Cannot perform handshake with CPO with key [%s] as no outgoing token has been set."
					.formatted(key);
			LOG.error(message);
			throw new RuntimeException(message); // TODO
		}
		String versionUrl = registeredCPO.getVersionUrl();
		if (versionUrl == null) {
			String message = "Cannot perform handshake with CPO with key [%s] as no version URL has been set."
					.formatted(key);
			LOG.error(message);
			throw new RuntimeException(message); // TODO
		}
		LOG.debug("Calling CPO version URL [{}]...", versionUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity entity = new HttpEntity<>(headers);
		Version[] versions = restTemplate
				.exchange(versionUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<Response<Version[]>>() {
				}).getBody().data();
		Version version = pickVersion(versions);

		LOG.debug("Calling CPO version details URL [{}]...", version.url());
		VersionDetails versionDetails = restTemplate.exchange(version.url(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Response<VersionDetails>>() {
				}).getBody().data();
		if (versionDetails.version() == VersionNumber.V2_1_1) {
			String credentialsUrl = null;
			for (Endpoint endpoint : versionDetails.endpoints()) {
				if (endpoint.identifier() == ModuleID.CredentialsRegistration) {
					credentialsUrl = endpoint.url();
				}
			}
			String incomingToken = UUID.randomUUID().toString();
			RegisteredCPO registeredCPOWithIncomingToken = registeredOperatorMapper.updateIncomingToken(registeredCPO,
					incomingToken);
			updateRegisteredCPO(registeredCPOWithIncomingToken); // TODO Remove token in case there is an error
			Image image = new Image(null, null, null, null, null, null);
			BusinessDetails businessDetails = new BusinessDetails(emspInfo.get().getName(),
					emspInfo.get().getWebsiteUrl(), image);
			Credentials credentials = new Credentials(incomingToken, externalOcpiApiUrl + "/ocpi/emsp/versions",
					businessDetails, emspInfo.get().getPartyId(), emspInfo.get().getCountryCode());
			LOG.debug("Calling CPO Credentials URL [{}]...", credentialsUrl);
			entity = new HttpEntity<>(credentials, headers);
			Credentials emspCredentials = restTemplate.exchange(credentialsUrl, HttpMethod.POST, entity,
					new ParameterizedTypeReference<Response<Credentials>>() {
					}).getBody().data();
			RegisteredCPOV211 registeredCPOV211 = registeredOperatorMapper.toRegisteredCPOV211(registeredCPO,
					credentialsUrl, incomingToken, emspCredentials.token());
			updateRegisteredCPO(registeredCPOV211);
		}
	}

	@Override
	public void performHandshakeWithEMSP(String key) {
		RegisteredEMSP registeredEMSP = (RegisteredEMSP) registeredOperatorStore.findByKey(key).orElseThrow(() -> {
			String message = "Cannot perform handshake with EMSP with key [%s] as it cannot be found.".formatted(key);
			LOG.error(message);
			return new RuntimeException(message); // TODO
		});
		String token = registeredEMSP.getOutgoingToken();
		if (token == null) {
			String message = "Cannot perform handshake with EMSP with key [%s] as no outgoing token has been set."
					.formatted(key);
			LOG.error(message);
			throw new RuntimeException(message); // TODO
		}
		String versionUrl = registeredEMSP.getVersionUrl();
		if (versionUrl == null) {
			String message = "Cannot perform handshake with EMSP with key [%s] as no version URL has been set."
					.formatted(key);
			LOG.error(message);
			throw new RuntimeException(message); // TODO
		}

		LOG.debug("Calling EMSP version URL [{}]...", versionUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity entity = new HttpEntity<>(headers);
		Version[] versions = restTemplate
				.exchange(versionUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<Response<Version[]>>() {
				}).getBody().data();
		Version version = pickVersion(versions);

		LOG.debug("Calling EMSP version details URL [{}]...", version.url());
		VersionDetails versionDetails = restTemplate.exchange(version.url(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Response<VersionDetails>>() {
				}).getBody().data();
		if (versionDetails.version() == VersionNumber.V2_1_1) {
			String credentialsUrl = null;
			for (Endpoint endpoint : versionDetails.endpoints()) {
				if (endpoint.identifier() == ModuleID.CredentialsRegistration) {
					credentialsUrl = endpoint.url();
				}
			}
			String incomingToken = UUID.randomUUID().toString();
			RegisteredEMSP registeredEMSPWithIncomingToken = registeredOperatorMapper
					.updateIncomingToken(registeredEMSP, incomingToken);
			updateRegisteredEMSP(registeredEMSPWithIncomingToken); // TODO Remove token in case there is an error
			Image image = new Image(null, null, null, null, null, null);
			BusinessDetails businessDetails = new BusinessDetails(cpoInfo.get().getName(),
					cpoInfo.get().getWebsiteUrl(), image);
			Credentials credentials = new Credentials(incomingToken, externalOcpiApiUrl + "/ocpi/cpo/versions",
					businessDetails, cpoInfo.get().getPartyId(), cpoInfo.get().getCountryCode());
			LOG.debug("Calling EMSP Credentials URL [{}]...", credentialsUrl);
			entity = new HttpEntity<>(credentials, headers);
			Credentials emspCredentials = restTemplate.exchange(credentialsUrl, HttpMethod.POST, entity,
					new ParameterizedTypeReference<Response<Credentials>>() {
					}).getBody().data();
			RegisteredEMSPV211 registeredEMSPV211 = registeredOperatorMapper.toRegisteredEMSPV211(registeredEMSP,
					credentialsUrl, incomingToken, emspCredentials.token());
			updateRegisteredEMSP(registeredEMSPV211);
		}
	}

	private Version pickVersion(Version[] versions) {
		return Stream.of(versions).filter(v -> v.version() == VersionNumber.V2_1_1).findFirst().orElseThrow();
	}

	@Override
	public RegisteredCPO finalizeHandshakeWithCPO(String key, Credentials credentials) {
		RegisteredCPO registeredCPO = (RegisteredCPO) registeredOperatorStore.findByKey(key).orElseThrow(() -> {
			String message = "Cannot finalize handshake with CPO with key [%s] as it cannot be found.".formatted(key);
			LOG.error(message);
			return new RuntimeException(message); // TODO
		});
		LOG.debug("Calling CPO version URL [{}]...", credentials.versionUrl());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + credentials.token());
		HttpEntity entity = new HttpEntity<>(headers);
		Version[] versions = restTemplate.exchange(credentials.versionUrl(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Response<Version[]>>() {
				}).getBody().data();
		Version version = pickVersion(versions);

		LOG.debug("Calling CPO version details URL [{}]...", version.url());
		VersionDetails versionDetails = restTemplate.exchange(version.url(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Response<VersionDetails>>() {
				}).getBody().data();
		String credentialsUrl = null;
		for (Endpoint endpoint : versionDetails.endpoints()) {
			if (endpoint.identifier() == ModuleID.CredentialsRegistration) {
				credentialsUrl = endpoint.url();
			}
		}
		String incomingToken = UUID.randomUUID().toString();
		RegisteredCPOV211 registeredCPOV211 = registeredOperatorMapper.toRegisteredCPOV211(registeredCPO,
				credentialsUrl, incomingToken, credentials.token());
		return updateRegisteredCPO(registeredCPOV211);
	}

	@Override
	public RegisteredEMSP finalizeHandshakeWithEMSP(String emspKey, @Valid Credentials credentials) {
		RegisteredEMSP registeredEMSP = (RegisteredEMSP) registeredOperatorStore.findByKey(emspKey).orElseThrow(() -> {
			String message = "Cannot finalize handshake with EMSP with key [%s] as it cannot be found."
					.formatted(emspKey);
			LOG.error(message);
			return new RuntimeException(message); // TODO
		});
		LOG.debug("Calling EMSP version URL [{}]...", credentials.versionUrl());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + credentials.token());
		HttpEntity entity = new HttpEntity<>(headers);
		Version[] versions = restTemplate.exchange(credentials.versionUrl(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Response<Version[]>>() {
				}).getBody().data();
		Version version = pickVersion(versions);

		LOG.debug("Calling EMSP version details URL [{}]...", version.url());
		VersionDetails versionDetails = restTemplate.exchange(version.url(), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Response<VersionDetails>>() {
				}).getBody().data();
		String credentialsUrl = null;
		for (Endpoint endpoint : versionDetails.endpoints()) {
			if (endpoint.identifier() == ModuleID.CredentialsRegistration) {
				credentialsUrl = endpoint.url();
			}
		}
		String incomingToken = UUID.randomUUID().toString();
		RegisteredEMSPV211 registeredEMSPV211 = registeredOperatorMapper.toRegisteredEMSPV211(registeredEMSP,
				credentialsUrl, incomingToken, credentials.token());
		return updateRegisteredEMSP(registeredEMSPV211);
	}

}
