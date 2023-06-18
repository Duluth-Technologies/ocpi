package com.duluthtechnologies.ocpi.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class EvseCreationForm {

	@Size(max = 255)
	private String key;

	@Size(max = 39)
	private String ocpiId;

	@Size(max = 48)
	private String evseId;

	@Valid
	private List<ConnectorCreationForm> connectors;

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

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public List<ConnectorCreationForm> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<ConnectorCreationForm> connectors) {
		this.connectors = connectors;
	}

}
