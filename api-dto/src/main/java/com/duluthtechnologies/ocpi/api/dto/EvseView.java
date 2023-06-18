package com.duluthtechnologies.ocpi.api.dto;

import java.util.List;

public class EvseView {

	private String key;

	private String ocpiId;

	private String evseId;

	private List<ConnectorView> connectors;

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

	public List<ConnectorView> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<ConnectorView> connectors) {
		this.connectors = connectors;
	}

}
