package com.duluthtechnologies.ocpi.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.duluthtechnologies.ocpi.api.client.OcpiApiClientException;
import com.duluthtechnologies.ocpi.api.client.OcpiEMSPApiV211Client;
import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Status;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService;
import com.duluthtechnologies.ocpi.core.store.ChargingSessionStore;
import com.duluthtechnologies.ocpi.core.store.ConnectorStore;
import com.duluthtechnologies.ocpi.core.store.LocationStore;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.model.v211.Session;
import com.duluthtechnologies.ocpi.service.helper.KeyGenerator;
import com.duluthtechnologies.ocpi.service.mapper.ChargingSessionMapper;
import com.duluthtechnologies.ocpi.service.model.impl.ChargingSessionImpl;
import com.duluthtechnologies.ocpi.service.model.impl.RegisteredEMSPChargingSessionImpl;
import com.duluthtechnologies.ocpi.service.security.ApplySecurityFiltering;
import com.duluthtechnologies.ocpi.service.security.SecurityContextFiltered;
import com.duluthtechnologies.ocpi.service.security.filter.RegisteredOperatorKeyFilter;

import jakarta.transaction.Transactional;

@Component
@Validated
public class ChargingSessionServiceImpl implements ChargingSessionService {

	private static final Logger LOG = LoggerFactory.getLogger(ChargingSessionServiceImpl.class);

	private final ChargingSessionStore chargingSessionStore;

	private final LocationStore locationStore;

	private final LocationServiceImpl locationServiceImpl;

	private final ConnectorStore connectorStore;

	private final ChargingSessionMapper chargingSessionMapper;

	private final RegisteredOperatorStore registeredOperatorStore;

	private final OcpiEMSPApiV211Client ocpiEMSPApiV211Client;

	private final Optional<CPOInfo> cpoInfo;

	public ChargingSessionServiceImpl(ChargingSessionStore chargingSessionStore, ConnectorStore connectorStore,
			RegisteredOperatorStore registeredOperatorStore, ChargingSessionMapper chargingSessionMapper,
			OcpiEMSPApiV211Client ocpiEMSPApiV211Client, LocationStore locationStore,
			LocationServiceImpl locationServiceImpl, Optional<CPOInfo> cpoInfo) {
		super();
		this.chargingSessionStore = chargingSessionStore;
		this.locationStore = locationStore;
		this.locationServiceImpl = locationServiceImpl;
		this.connectorStore = connectorStore;
		this.chargingSessionMapper = chargingSessionMapper;
		this.registeredOperatorStore = registeredOperatorStore;
		this.ocpiEMSPApiV211Client = ocpiEMSPApiV211Client;
		this.cpoInfo = cpoInfo;
	}

	@Override
	@ApplySecurityFiltering
	public Page<RegisteredEMSPChargingSession> findRegisteredEMSPSessions(
			@SecurityContextFiltered(filter = RegisteredOperatorKeyFilter.class) String registeredEmspKey,
			Instant dateFrom, Instant dateTo, Integer offset, Integer limit) {
		return chargingSessionStore.findRegisteredEMSPChargingSessions(registeredEmspKey, dateFrom, dateTo, offset,
				limit);
	}

	@Override
	@Transactional
	public RegisteredEMSPChargingSession createRegisteredEMSPChargingSession(
			RegisteredEMSPChargingSessionCreationForm registeredEMSPChargingSessionCreationForm) {
		Connector connector = connectorStore.findByKey(registeredEMSPChargingSessionCreationForm.connectorKey())
				.orElseThrow(() -> {
					String message = "Cannot create RegisteredEMSPChargingSession [%s] as the connector with key [%s] doesn't exist."
							.formatted(registeredEMSPChargingSessionCreationForm,
									registeredEMSPChargingSessionCreationForm.connectorKey());
					LOG.error(message);
					return new RuntimeException(message);
				});

		RegisteredEMSP registeredEMSP = (RegisteredEMSP) registeredOperatorStore
				.findByKey(registeredEMSPChargingSessionCreationForm.registeredEmspKey()).orElseThrow(() -> {
					String message = "Cannot create RegisteredEMSPChargingSession [%s] as the Registered EMSP with key [%s] doesn't exist."
							.formatted(registeredEMSPChargingSessionCreationForm,
									registeredEMSPChargingSessionCreationForm.registeredEmspKey());
					LOG.error(message);
					return new RuntimeException(message);
				});

		RegisteredEMSPChargingSessionImpl registeredEMSPChargingSessionImpl = chargingSessionMapper
				.toRegisteredEMSPChargingSessionImpl(registeredEMSPChargingSessionCreationForm);
		if (registeredEMSPChargingSessionImpl.getKey() == null) {
			registeredEMSPChargingSessionImpl.setKey(KeyGenerator.generateKey(RegisteredEMSPChargingSession.class));
		}
		if (registeredEMSPChargingSessionImpl.getOcpiId() == null) {
			registeredEMSPChargingSessionImpl.setOcpiId(UUID.randomUUID().toString());
		}
		registeredEMSPChargingSessionImpl.setConnector(connector);
		registeredEMSPChargingSessionImpl.setRegisteredEMSP(registeredEMSP);
		RegisteredEMSPChargingSession registeredEMSPChargingSession = chargingSessionStore
				.create(registeredEMSPChargingSessionImpl);

		// At this point the transaction is not commited so it will not be if there is
		// an issue on EMSP side.
		createOrUpdateChargingSessionOnEMSP(registeredEMSP, registeredEMSPChargingSession);
		return registeredEMSPChargingSession;
	}

	@Override
	@Transactional
	public RegisteredEMSPChargingSession updateRegisteredEMSPChargingSession(
			RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm) {
		LOG.debug("Updating Registered EMSP Charging Session [{}]...", registeredEMSPChargingSessionForm);
		RegisteredEMSPChargingSession registeredEMSPChargingSession = (RegisteredEMSPChargingSession) chargingSessionStore
				.findByKey(registeredEMSPChargingSessionForm.key()).orElseThrow(() -> {
					String message = "Cannot update Charging Session with key [%s] as it cannot be found."
							.formatted(registeredEMSPChargingSessionForm.key());
					LOG.error(message);
					return new RuntimeException(message);
				});

		// Make sure the update respect business rules
		validateUpdate(registeredEMSPChargingSession, registeredEMSPChargingSessionForm);

		// Map
		RegisteredEMSPChargingSessionImpl registeredEMSPChargingSessionImpl = chargingSessionMapper
				.toRegisteredEMSPChargingSessionImpl(registeredEMSPChargingSessionForm);
		registeredEMSPChargingSessionImpl.setConnector(registeredEMSPChargingSession.getConnector());
		registeredEMSPChargingSessionImpl.setRegisteredEMSP(registeredEMSPChargingSession.getRegisteredEMSP());
		registeredEMSPChargingSession = (RegisteredEMSPChargingSession) chargingSessionStore
				.update(registeredEMSPChargingSessionImpl);

		// At this point the transaction is not commited so it will not be if there is
		// an issue on EMSP side.
		createOrUpdateChargingSessionOnEMSP(registeredEMSPChargingSession.getRegisteredEMSP(),
				registeredEMSPChargingSession);
		return registeredEMSPChargingSession;
	}

	private void validateUpdate(RegisteredEMSPChargingSession existingSession, RegisteredEMSPChargingSessionForm form) {

		// Rule 1: ocpiId, registeredEmspKey and connectorKey can never be changed
		if (!existingSession.getOcpiId().equals(form.ocpiId())) {
			String message = "Cannot update Charging Session with OCPI id [%s] as it is already set to [%s]."
					.formatted(form.ocpiId(), existingSession.getOcpiId());
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}

		if (!existingSession.getRegisteredEMSP().getKey().equals(form.registeredEmspKey())) {
			String message = "Cannot update Charging Session with Registered EMSP key [%s] as it is already set to [%s]."
					.formatted(form.registeredEmspKey(), existingSession.getRegisteredEMSP().getKey());
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}

		if (!existingSession.getConnector().getKey().equals(form.connectorKey())) {
			String message = "Cannot update Charging Session with Connector key [%s] as it is already set to [%s]."
					.formatted(form.connectorKey(), existingSession.getConnector().getKey());
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}

		// Rule 2: when startDate, stopDate and disconnectDate are set, they cannot be
		// changed
		if (existingSession.getStartDate() != null
				&& !isWithinTolerance(existingSession.getStartDate(), form.startDate())) {
			String message = "Cannot update Charging Session with startDate [%s] as it is already set to [%s]."
					.formatted(form.startDate(), existingSession.getStartDate());
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		if (existingSession.getStopDate() != null
				&& !isWithinTolerance(existingSession.getStopDate(), form.stopDate())) {
			String message = "Cannot update Charging Session with stopDate [%s] as it is already set to [%s]."
					.formatted(form.stopDate(), existingSession.getStopDate());
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		if (existingSession.getDisconnectDate() != null
				&& !isWithinTolerance(existingSession.getDisconnectDate(), form.disconnectDate())) {
			String message = "Cannot update Charging Session with disconnectDate [%s] as it is already set to [%s]."
					.formatted(form.disconnectDate(), existingSession.getDisconnectDate());
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
	}

	private boolean isWithinTolerance(Instant existingDate, Instant newDate) {
		if (newDate == null)
			return false; // If the new date is not provided, it's not within tolerance

		long differenceInMillis = Math.abs(Duration.between(existingDate, newDate).toMillis());
		return differenceInMillis <= 1; // Checking if the difference is within the tolerance of 1ms
	}

	private void createOrUpdateChargingSessionOnEMSP(RegisteredEMSP registeredEMSP,
			RegisteredEMSPChargingSession registeredEMSPChargingSession) {
		LOG.debug(
				"Creating or Updating Registered EMSP Charging Session with OCPI id [{}] to Registered EMSP with key [{}]...",
				registeredEMSPChargingSession.getOcpiId(), registeredEMSP.getKey());
		try {
			Session session = chargingSessionMapper.toSessionV211(registeredEMSPChargingSession);
			if (registeredEMSP instanceof RegisteredEMSPV211 registeredEMSPV211) {

				ocpiEMSPApiV211Client.putSessionV211(registeredEMSPV211.getOutgoingToken(),
						registeredEMSPV211.getSessionsUrl(), cpoInfo.get().getCountryCode(), cpoInfo.get().getPartyId(),
						session.id(), session);

			}
		} catch (OcpiApiClientException e) {
			String message = "Exception caught while updating Registered EMSP Charging Session [%s] to Registered EMSP with key [%s]."
					.formatted(registeredEMSPChargingSession, registeredEMSP.getKey());
			LOG.error(message);
			throw new RuntimeException(message, e);
		}
	}

	@Override
	public List<ChargingSession> findChargingSessions(Instant dateFrom, Instant dateTo, Optional<String> connectorKey) {
		return chargingSessionStore.findChargingSessions(dateFrom, dateTo, connectorKey);
	}

	@Override
	public Optional<ChargingSession> findChargingSession(String countryCode, String partyId, String sessionOcpiId) {
		return chargingSessionStore.findChargingSessions(countryCode, partyId, sessionOcpiId);
	}

	@Override
	@Transactional
	@ApplySecurityFiltering
	public ChargingSession createChargingSession(
			@SecurityContextFiltered(filter = RegisteredOperatorKeyFilter.class) String registeredCpoKey,
			ChargingSessionFormWithLocation chargingSessionFormWithLocation) {
		LOG.debug("Creating Charging Session [{}] linked to Registered CPO with key [{}]...",
				chargingSessionFormWithLocation, registeredCpoKey);
		try {
			// First synchronize to make sure we have all connectors with the right status
			locationServiceImpl.synchronizeWithRegisteredCpo(registeredCpoKey);

			// Then retrieve the location
			Location location = locationStore
					.findByRegisteredCpoKeyAndOcpiId(registeredCpoKey, chargingSessionFormWithLocation.locationOcpiId())
					.orElseThrow(() -> {
						String message = "Cannot create Charging Session [%s] as no Location was found for Registered CPO with key [%s] and OCPI id [%s]."
								.formatted(chargingSessionFormWithLocation, registeredCpoKey,
										chargingSessionFormWithLocation.locationOcpiId());
						LOG.error(message);
						return new RuntimeException(message);
					});

			// Now find the connector that was used for the session
			Connector connector;
			List<Connector> unavailableConnectors = location.getEvses().stream()
					.flatMap(evse -> evse.getConnectors().stream()).filter(c -> c.getStatus() == Status.UNAVAILABLE)
					.toList();
			LOG.debug("Found [{}] connectors with satus [{}] for Location with key [{}].", unavailableConnectors.size(),
					Status.UNAVAILABLE, location.getKey());
			if (unavailableConnectors.isEmpty()) {
				String message = "Cannot create Charging Session [%s] for Location with key [%s] as not Connector has status [%s]."
						.formatted(chargingSessionFormWithLocation, location.getKey(), Status.UNAVAILABLE);
				LOG.error(message);
				throw new RuntimeException(message);
			} else if (unavailableConnectors.size() == 1) {
				connector = unavailableConnectors.get(0);
			} else {
				connector = findLastUnavailableConnector(unavailableConnectors);
			}

			ChargingSessionImpl chargingSessionImpl = chargingSessionMapper.toChargingSessionImpl(
					KeyGenerator.generateKey(ChargingSession.class), connector, chargingSessionFormWithLocation);
			ChargingSession chargingSession = chargingSessionStore.create(chargingSessionImpl);
			LOG.debug("Created Charging Session linked to Registered CPO [{}].", registeredCpoKey);
			return chargingSession;
		} catch (Exception e) {
			String message = "Exception caught while creating Charging Session [%s] linked to Registered CPO with key [%s]."
					.formatted(chargingSessionFormWithLocation, registeredCpoKey);
			LOG.error(message);
			throw new RuntimeException(message, e);
		}
	}

	private Connector findLastUnavailableConnector(List<Connector> unavailableConnectors) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	@ApplySecurityFiltering
	@Transactional
	public ChargingSession updateChargingSession(
			@SecurityContextFiltered(filter = RegisteredOperatorKeyFilter.class) String registeredCpoKey,
			String chargingSessionKey, ChargingSessionFormWithLocation chargingSessionFormWithLocation) {
		LOG.debug("Updating Charging Session with key [{}] with content [{}] linked to Registered CPO with key [{}]...",
				chargingSessionKey, chargingSessionFormWithLocation, registeredCpoKey);
		try {
			ChargingSession chargingSession = chargingSessionStore.findByKey(chargingSessionKey).orElseThrow(() -> {
				String message = "Cannot update Charging Session with key [%s] as it cannot be found."
						.formatted(chargingSessionKey);
				LOG.error(message);
				return new RuntimeException(message);
			});
			// We force the update to use the same connector as -1. It is complicated to
			// find
			// the connector -2. It is not possible at the Domain Level to change connector
			// of a session
			ChargingSessionImpl chargingSessionImpl = chargingSessionMapper.toChargingSessionImpl(chargingSessionKey,
					chargingSession.getConnector(), chargingSessionFormWithLocation);
			chargingSession = chargingSessionStore.update(chargingSessionImpl);
			LOG.debug("Updated Charging Session with key [{}].", chargingSessionKey);
			return chargingSession;
		} catch (Exception e) {
			String message = "Exception caught while updating Charging Session with key [%s] with content [%s] linked to Registered CPO with key [%s]."
					.formatted(chargingSessionKey, chargingSessionFormWithLocation, registeredCpoKey);
			LOG.error(message);
			throw new RuntimeException(message, e);
		}
	}

	@Override
	@ApplySecurityFiltering
	@Transactional
	public ChargingSession patchRegisteredCPOLocation(
			@SecurityContextFiltered(filter = RegisteredOperatorKeyFilter.class) String registeredCpoKey,
			String chargingSessionKey, ChargingSessionFormWithLocation chargingSessionFormWithLocation) {
		ChargingSession chargingSession = chargingSessionStore.findByKey(chargingSessionKey).orElseThrow(() -> {
			String message = "Cannot update Charging Session with key [%s] as it cannot be found."
					.formatted(chargingSessionKey);
			LOG.error(message);
			return new RuntimeException(message);
		});
		// We force the update to use the same connector as -1. It is complicated to
		// find
		// the connector -2. It is not possible at the Domain Level to change connector
		// of a session
		ChargingSessionImpl chargingSessionImpl = chargingSessionMapper.toChargingSessionImpl(chargingSessionKey,
				chargingSession.getConnector(), chargingSessionFormWithLocation);
		return chargingSessionStore.patch(chargingSessionImpl);
	}

	@Override
	public ChargingSession getByKey(String key) {
		return chargingSessionStore.findByKey(key).orElseThrow(() -> {
			String message = "Cannot get Charging Session with key [%s] as it cannot be found.".formatted(key);
			LOG.error(message);
			return new RuntimeException(message);
		});
	}

}
