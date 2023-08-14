package com.duluthtechnologies.ocpi.service.mapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.ChargingSession.Cost;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService.ChargingSessionFormWithLocation;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService.RegisteredEMSPChargingSessionCreationForm;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService.RegisteredEMSPChargingSessionForm;
import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod;
import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod.CdrDimension;
import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod.CdrDimension.CdrDimensionType;
import com.duluthtechnologies.ocpi.model.v211.Session.SessionStatus;
import com.duluthtechnologies.ocpi.service.model.impl.ChargingSessionImpl;
import com.duluthtechnologies.ocpi.service.model.impl.RegisteredEMSPChargingSessionImpl;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = LocationMapper.class)
public abstract class ChargingSessionMapper {

	@Autowired
	protected LocationMapper locationMapper;

	public abstract RegisteredEMSPChargingSessionImpl toRegisteredEMSPChargingSessionImpl(
			RegisteredEMSPChargingSessionCreationForm registeredEMSPChargingSessionCreationForm);

	public abstract RegisteredEMSPChargingSessionImpl toRegisteredEMSPChargingSessionImpl(
			RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm);

	@Mapping(target = "id", source = "ocpiId")
	@Mapping(target = "startDateTime", source = "startDate")
	@Mapping(target = "endDateTime", source = "disconnectDate")
	@Mapping(target = "kwh", source = "energyDeliveredInWh", qualifiedByName = "toKwh")
	@Mapping(target = "location", source = "connector.evse.location")
	@Mapping(target = "status", source = "chargingSession", qualifiedByName = "toStatus")
	@Mapping(target = "chargingPeriods", source = "chargingSession", qualifiedByName = "toChargingPeriods")
	@Mapping(target = "currency", source = "chargingSession.cost.currency.currencyCode")
	@Mapping(target = "totalCost", source = "chargingSession.cost", qualifiedByName = "toTotalCost")
	@Mapping(target = "lastUpdated", source = "lastModifiedDate")
	public abstract com.duluthtechnologies.ocpi.model.v211.Session toSessionV211(ChargingSession chargingSession);

	protected LocalDateTime toLocalDateTime(Instant instant) {
		if (instant == null) {
			return null;
		}
		return instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
	}

	@Named("toStatus")
	protected SessionStatus toStatus(ChargingSession session) {
		// If no start date the Session is still pending
		if (session.getStartDate() == null) {
			return SessionStatus.PENDING;
		}
		// There is a start date but connector is still connected, so session is active
		if (session.getStartDate() != null && session.getDisconnectDate() == null) {
			return SessionStatus.ACTIVE;
		}
		// All the dates are set, which means the session is completed
		if (session.getStartDate() != null && session.getStopDate() != null && session.getDisconnectDate() != null) {
			return SessionStatus.COMPLETED;
		}
		// Any other combination means the Session is invalid
		return SessionStatus.INVALID;
	}

	@Named("toKwh")
	protected Double toKwh(Integer eneryDeliveredInWh) {
		if (eneryDeliveredInWh == null) {
			return null;
		}
		return Double.valueOf(eneryDeliveredInWh) / 1000;
	}

	@Named("toChargingPeriods")
	protected List<ChargingPeriod> toChargingPeriods(ChargingSession session) {
		List<ChargingPeriod> result = new LinkedList<>();
		if (session.getStartDate() != null) {
			ChargingPeriod chargingPeriod = new ChargingPeriod(toLocalDateTime(session.getStartDate()),
					List.of(new CdrDimension(CdrDimensionType.ENERGY, toKwh(session.getEnergyDeliveredInWh())),
							new CdrDimension(CdrDimensionType.TIME, toHourInterval(session.getStartDate(),
									session.getStopDate() == null ? Instant.now() : session.getStopDate()))));
			result.add(chargingPeriod);
		}
		if (session.getStopDate() != null) {
			ChargingPeriod chargingPeriod = new ChargingPeriod(toLocalDateTime(session.getStopDate()),
					List.of(new CdrDimension(CdrDimensionType.PARKING_TIME, toHourInterval(session.getStopDate(),
							session.getDisconnectDate() == null ? Instant.now() : session.getDisconnectDate()))));
			result.add(chargingPeriod);
		}
		return result;
	}

	private static Double toHourInterval(Instant fromInstant, Instant toInstant) {
		Duration duration = Duration.between(fromInstant, toInstant);
		double hours = duration.getSeconds() / 3600.0; // 3600 seconds in an hour
		return Math.round(hours * 10000) / 10000.0; // rounding to 4 decimal places
	}

	@Named("toTotalCost")
	protected Double toTotalCost(Cost cost) {
		if (cost == null) {
			return null;
		}
		return Double.valueOf(cost.getFractionalAmount()) / Math.pow(10, cost.getCurrency().getDefaultFractionDigits());
	}

	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "lastModifiedDate", ignore = true)
	@Mapping(target = "key", source = "key")
	public abstract ChargingSessionImpl toChargingSessionImpl(String key, Connector connector,
			ChargingSessionFormWithLocation chargingSessionCreationForm);

	protected com.duluthtechnologies.ocpi.model.v211.Location toLocationV211(Location location) {
		if (location == null) {
			return null;
		}
		return locationMapper.toLocationV211(location, null);

	}

}
