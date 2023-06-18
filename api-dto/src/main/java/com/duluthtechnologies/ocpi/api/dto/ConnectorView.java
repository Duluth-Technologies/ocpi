package com.duluthtechnologies.ocpi.api.dto;

import java.time.Instant;

import com.duluthtechnologies.ocpi.core.model.Connector;

public class ConnectorView {

	private String key;

	private String connectorId;

	private Connector.Type type;

	private Connector.Format format;

	private Connector.PowerType powerType;

	private int maximumVoltage;

	private int maximumAmperage;

	private Connector.Status status;

	private Instant lastModifiedDate;

	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

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

	public Connector.Type getType() {
		return type;
	}

	public void setType(Connector.Type type) {
		this.type = type;
	}

	public Connector.Format getFormat() {
		return format;
	}

	public void setFormat(Connector.Format format) {
		this.format = format;
	}

	public Connector.PowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(Connector.PowerType powerType) {
		this.powerType = powerType;
	}

	public int getMaximumVoltage() {
		return maximumVoltage;
	}

	public void setMaximumVoltage(int maximumVoltage) {
		this.maximumVoltage = maximumVoltage;
	}

	public int getMaximumAmperage() {
		return maximumAmperage;
	}

	public void setMaximumAmperage(int maximumAmperage) {
		this.maximumAmperage = maximumAmperage;
	}

	public Connector.Status getStatus() {
		return status;
	}

	public void setStatus(Connector.Status status) {
		this.status = status;
	}

}
