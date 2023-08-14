package com.duluthtechnologies.ocpi.core.model;

import java.time.Instant;
import java.util.Currency;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;

public interface ChargingSession {

	@NotEmpty
	String getKey();

	@NotEmpty
	String getOcpiId();

	@Past
	@NotNull
	Instant getCreatedDate();

	@Past
	Instant getStartDate();

	@Past
	Instant getStopDate();

	@Past
	Instant getDisconnectDate();

	@NotNull
	Connector getConnector();

	@PositiveOrZero
	Integer getEnergyDeliveredInWh();

	@Past
	Instant getLastModifiedDate();

	@Valid
	Cost getCost();

	public interface Cost {

		@PositiveOrZero
		Integer getFractionalAmount();

		@NotNull
		Currency getCurrency();
	}

	@AssertTrue(message = "stopDate should be after startDate.")
	default boolean isStopDateAfterStartDate() {
		if (getStopDate() != null && getStartDate() != null) {
			return !getStopDate().isBefore(getStartDate());
		}
		return true;
	}

	@AssertTrue(message = "disconnectDate should be after stopDate if stopDate is set.")
	default boolean isDisconnectDateAfterStopDate() {
		if (getDisconnectDate() != null && getStopDate() != null) {
			return !getDisconnectDate().isBefore(getStopDate());
		}
		return true;
	}

	@AssertTrue(message = "stopDate can only be set if startDate is already set.")
	default boolean isStartDatePresentBeforeStopDate() {
		return !(getStopDate() != null && getStartDate() == null);
	}

	@AssertTrue(message = "disconnectDate cannot be set if only startDate is set but not stopDate.")
	default boolean isStopDatePresentBeforeDisconnectDate() {
		return !(getDisconnectDate() != null && getStartDate() != null && getStopDate() == null);
	}

}
