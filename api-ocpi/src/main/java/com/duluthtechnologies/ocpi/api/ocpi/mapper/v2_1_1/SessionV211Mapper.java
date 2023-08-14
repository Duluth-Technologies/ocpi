package com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.ChargingSession.Cost;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService.ChargingSessionFormWithLocation;
import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod;
import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod.CdrDimension;
import com.duluthtechnologies.ocpi.model.v211.ChargingPeriod.CdrDimension.CdrDimensionType;
import com.duluthtechnologies.ocpi.model.v211.Session;
import com.duluthtechnologies.ocpi.model.v211.Session.SessionStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class SessionV211Mapper {

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
	public abstract com.duluthtechnologies.ocpi.model.v211.Session toDTO(ChargingSession chargingSession);

	public abstract List<com.duluthtechnologies.ocpi.model.v211.Session> toSessions(
			List<? extends ChargingSession> content);

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

	@Mapping(target = "ocpiId", source = "id")
	@Mapping(target = "key", ignore = true)
	@Mapping(target = "startDate", source = "startDateTime")
	@Mapping(target = "disconnectDate", source = "endDateTime")
	@Mapping(target = "stopDate", source = "chargingPeriods", qualifiedByName = "toStopDate")
	@Mapping(target = "locationOcpiId", source = "session.location.id")
	@Mapping(target = "energyDeliveredInWh", source = "kwh", qualifiedByName = "fromKwh")
	@Mapping(target = "cost", source = "session", qualifiedByName = "fromTotalCost")
	public abstract ChargingSessionFormWithLocation toChargingSessionFormWithLocation(Session session);

	@Named("toStopDate")
	protected Instant toStopDate(List<ChargingPeriod> chargingPeriods) {
		if (chargingPeriods == null) {
			return null;
		}
		return chargingPeriods.stream()
				.filter(cp -> cp.dimensions().stream().anyMatch(d -> d.type().equals(CdrDimensionType.PARKING_TIME)))
				.map(ChargingPeriod::startDateTime).map(this::fromLocalDateTime).sorted().findFirst().orElse(null);
	}

	protected Instant fromLocalDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.atOffset(ZoneOffset.UTC).toInstant();
	}

	@Named("fromKwh")
	protected Integer fromKwh(Double kwh) {
		if (kwh == null) {
			return null;
		}
		return (int) Math.floor(kwh * 1000);
	}

	@Named("fromTotalCost")
	protected Cost fromTotalCost(Session session) {
		if (session.currency() == null) {
			return null;
		}
		Currency currency = Currency.getInstance(session.currency());
		Integer fractionalAmount = (int) (session.totalCost() * Math.pow(10, currency.getDefaultFractionDigits()));
		return new CostImpl(fractionalAmount, currency);
	}

	private static final record CostImpl(Integer fractionalAmount, Currency currency) implements Cost {

		@Override
		public @PositiveOrZero Integer getFractionalAmount() {
			return fractionalAmount;
		}

		@Override
		public @NotNull Currency getCurrency() {
			return currency;
		}
	}

}
