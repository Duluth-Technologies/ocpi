package com.duluthtechnologies.ocpi.api.dto;

import java.time.Instant;
import java.util.Currency;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = RegisteredEMSPChargingSessionView.class, name = "registered_emsp_charging_session"),
		@Type(value = ChargingSessionView.class, name = "charging_session") })
public class ChargingSessionView {

	@NotEmpty
	private String key;

	@NotEmpty
	private String ocpiId;

	@Past
	@NotNull
	private Instant createdDate;

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

	@Past
	private Instant lastModifiedDate;

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

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
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

	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	}
}
