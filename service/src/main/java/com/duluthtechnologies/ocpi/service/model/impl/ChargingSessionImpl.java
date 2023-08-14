package com.duluthtechnologies.ocpi.service.model.impl;

import java.time.Instant;
import java.util.Currency;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.Connector;

public class ChargingSessionImpl implements ChargingSession {

	private String key;
	private String ocpiId;
	private Instant createdDate;
	private Instant startDate;
	private Instant stopDate;
	private Instant disconnectDate;
	private Connector connector;
	private Integer energyDeliveredInWh;
	private Instant lastModifiedDate;
	private Cost cost;

	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getOcpiId() {
		return ocpiId;
	}

	public void setOcpiId(String ocpiId) {
		this.ocpiId = ocpiId;
	}

	@Override
	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	@Override
	public Instant getStopDate() {
		return stopDate;
	}

	public void setStopDate(Instant stopDate) {
		this.stopDate = stopDate;
	}

	@Override
	public Instant getDisconnectDate() {
		return disconnectDate;
	}

	public void setDisconnectDate(Instant disconnectDate) {
		this.disconnectDate = disconnectDate;
	}

	@Override
	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	@Override
	public Integer getEnergyDeliveredInWh() {
		return energyDeliveredInWh;
	}

	public void setEnergyDeliveredInWh(Integer energyDeliveredInWh) {
		this.energyDeliveredInWh = energyDeliveredInWh;
	}

	@Override
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public Cost getCost() {
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	private static class CostImpl implements Cost {

		private Integer fractionalAmount;
		private Currency currency;

		@Override
		public Integer getFractionalAmount() {
			return fractionalAmount;
		}

		public void setFractionalAmount(Integer fractionalAmount) {
			this.fractionalAmount = fractionalAmount;
		}

		@Override
		public Currency getCurrency() {
			return currency;
		}

		public void setCurrency(Currency currency) {
			this.currency = currency;
		}
	}
}