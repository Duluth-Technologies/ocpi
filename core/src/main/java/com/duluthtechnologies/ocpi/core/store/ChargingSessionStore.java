package com.duluthtechnologies.ocpi.core.store;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;

public interface ChargingSessionStore {

	Page<RegisteredEMSPChargingSession> findRegisteredEMSPChargingSessions(String registeredEMSPKey, Instant dateFrom,
			Instant dateTo, Integer offset, Integer limit);

	RegisteredEMSPChargingSession create(RegisteredEMSPChargingSession registeredEMSPChargingSession);

	ChargingSession create(ChargingSession chargingSession);

	List<ChargingSession> findChargingSessions(Instant dateFrom, Instant dateTo, Optional<String> connectorKey);

	Optional<ChargingSession> findChargingSessions(String countryCode, String partyId, String ocpiId);

	Optional<ChargingSession> findByKey(String chargingSessionKey);

	ChargingSession update(ChargingSession chargingSession);

	ChargingSession patch(ChargingSession chargingSession);

}
