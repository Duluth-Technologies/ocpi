package com.duluthtechnologies.ocpi.service.model.impl;

import java.time.Instant;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;

public class ConnectorImpl implements Connector {

	private String key;

	private String connectorId;

	private Type type;

	private Format format;

	private PowerType powerType;

	private Integer maximumVoltage;

	private Integer maximumAmperage;

	private Status status;

	private Instant lastModifiedDate;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public PowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(PowerType powerType) {
		this.powerType = powerType;
	}

	public Integer getMaximumVoltage() {
		return maximumVoltage;
	}

	public void setMaximumVoltage(Integer maximumVoltage) {
		this.maximumVoltage = maximumVoltage;
	}

	public Integer getMaximumAmperage() {
		return maximumAmperage;
	}

	public void setMaximumAmperage(Integer maximumAmperage) {
		this.maximumAmperage = maximumAmperage;
	}

	@Override
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public Evse getEvse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
