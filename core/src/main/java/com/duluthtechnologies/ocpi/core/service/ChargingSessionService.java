package com.duluthtechnologies.ocpi.core.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.ChargingSession.Cost;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface ChargingSessionService {

	public static final record RegisteredEMSPChargingSessionCreationForm(@NotEmpty String key, @NotEmpty String ocpiId,
			@NotEmpty String registeredEmspKey, @NotNull String connectorKey) {
	}

	public static final record RegisteredEMSPChargingSessionForm(@NotEmpty String key, @NotEmpty String ocpiId,
			@NotEmpty String registeredEmspKey, @NotNull String connectorKey, Instant startDate, Instant stopDate,
			Instant disconnectDate, Integer energyDeliveredInWh, Cost cost) {
	}

	public static final record ChargingSessionFormWithLocation(@NotEmpty String key, @NotEmpty String ocpiId,
			Instant startDate, Instant stopDate, Instant disconnectDate, String locationOcpiId,
			Integer energyDeliveredInWh, Cost cost) {
	}

	Page<RegisteredEMSPChargingSession> findRegisteredEMSPSessions(String registeredEmspKey, Instant dateFrom,
			Instant dateTo, Integer offset, Integer limit);

	RegisteredEMSPChargingSession createRegisteredEMSPChargingSession(
			RegisteredEMSPChargingSessionCreationForm registeredEMSPChargingSessionCreationForm);

	RegisteredEMSPChargingSession updateRegisteredEMSPChargingSession(
			RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm);

	List<ChargingSession> findChargingSessions(Instant dateFrom, Instant dateTo, Optional<String> connectorKey);

	Optional<ChargingSession> findChargingSession(String countryCode, String partyId, String sessionId);

	ChargingSession createChargingSession(String registeredCPOKey,
			ChargingSessionFormWithLocation chargingSessionCreationForm);

	ChargingSession updateChargingSession(String registeredCpoKey, String chargingSessionKey,
			ChargingSessionFormWithLocation chargingSessionCreationForm);

	ChargingSession patchRegisteredCPOLocation(String registeredCpoKey, @NotEmpty String key,
			ChargingSessionFormWithLocation chargingSessionFormWithLocation);

	ChargingSession getByKey(String key);

}
