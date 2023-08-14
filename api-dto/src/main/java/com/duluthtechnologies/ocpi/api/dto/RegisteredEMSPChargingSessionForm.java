package com.duluthtechnologies.ocpi.api.dto;

import java.time.Instant;
import java.util.Currency;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;

public class RegisteredEMSPChargingSessionForm {

	@NotEmpty
	private String key;

	@NotEmpty
	private String ocpiId;

	@NotEmpty
	private String registeredEmspKey;

	@Past
	private Instant startDate;

	@Past
	private Instant stopDate;

	@Past
	private Instant disconnectDate;

	@NotNull
	private String connectorKey;

	@NotNull
	@PositiveOrZero
	private Integer energyDeliveredInWh;

	@Valid
	private Cost cost;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOcpiId() {
		return ocpiId;
	}

	public void setOcpiId(String ocpiId) {
		this.ocpiId = ocpiId;
	}

	public String getRegisteredEmspKey() {
		return registeredEmspKey;
	}

	public void setRegisteredEmspKey(String registeredEmspKey) {
		this.registeredEmspKey = registeredEmspKey;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public Instant getStopDate() {
		return stopDate;
	}

	public void setStopDate(Instant stopDate) {
		this.stopDate = stopDate;
	}

	public Instant getDisconnectDate() {
		return disconnectDate;
	}

	public void setDisconnectDate(Instant disconnectDate) {
		this.disconnectDate = disconnectDate;
	}

	public String getConnectorKey() {
		return connectorKey;
	}

	public void setConnectorKey(String connectorKey) {
		this.connectorKey = connectorKey;
	}

	public Integer getEnergyDeliveredInWh() {
		return energyDeliveredInWh;
	}

	public void setEnergyDeliveredInWh(Integer energyDeliveredInWh) {
		this.energyDeliveredInWh = energyDeliveredInWh;
	}

	public Cost getCost() {
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	public static class Cost {

		@PositiveOrZero
		private Integer fractionalAmount;

		@NotNull
		private Currency currency;

		public Integer getFractionalAmount() {
			return fractionalAmount;
		}

		public void setFractionalAmount(Integer fractionalAmount) {
			this.fractionalAmount = fractionalAmount;
		}

		public Currency getCurrency() {
			return currency;
		}

		public void setCurrency(Currency currency) {
			this.currency = currency;
		}

		@Override
		public String toString() {
			return "Cost [fractionalAmount=" + fractionalAmount + ", currency=" + currency + "]";
		}

	}

	@Override
	public String toString() {
		return "RegisteredEMSPChargingSessionForm [key=" + key + ", ocpiId=" + ocpiId + ", registeredEmspKey="
				+ registeredEmspKey + ", startDate=" + startDate + ", stopDate=" + stopDate + ", disconnectDate="
				+ disconnectDate + ", connectorKey=" + connectorKey + ", energyDeliveredInWh=" + energyDeliveredInWh
				+ ", cost=" + cost + "]";
	}

}
