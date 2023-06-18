package com.duluthtechnologies.ocpi.api.dto;

import com.duluthtechnologies.ocpi.core.model.Connector;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ConnectorCreationForm {

	@Size(max = 255)
	private String key;

	@NotEmpty
	@Size(max = 36)
	private String connectorId;

	@NotNull
	private Connector.Type type;

	@NotNull
	private Connector.Format format;

	@NotNull
	private Connector.PowerType powerType;

	private Connector.Status status;

	@Positive
	private int maximumVoltage;

	@Positive
	private int maximumAmperage;

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

	public Connector.Status getStatus() {
		return status;
	}

	public void setStatus(Connector.Status status) {
		this.status = status;
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

}
