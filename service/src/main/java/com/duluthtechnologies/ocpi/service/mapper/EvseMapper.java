package com.duluthtechnologies.ocpi.service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.EvseForm;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.service.model.impl.EvseImpl;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = ConnectorMapper.class)
public abstract class EvseMapper {

	@Autowired
	protected ConnectorMapper connectorMapper;

	@Mapping(target = "connectors", ignore = true)
	public abstract EvseImpl toEvse(EvseForm evseForm);

	@Mapping(target = "uid", source = "evse.ocpiId")
	@Mapping(target = "lastUpdated", source = "lastUpdatedTime")
	@Mapping(target = "connectors", source = "evse.connectors", qualifiedByName = "toConnectorsV211")
	@Mapping(target = "status", source = "evse", qualifiedByName = "toStatusV211")
	public abstract com.duluthtechnologies.ocpi.model.v211.EVSE toEvseV211(Evse evse, Instant lastUpdatedTime);

	@Named("toConnectorsV211")
	protected List<com.duluthtechnologies.ocpi.model.v211.Connector> toConnectorsV211(List<Connector> connectors) {
		if (connectors == null) {
			return null;
		}
		List<com.duluthtechnologies.ocpi.model.v211.Connector> result = new ArrayList<>();
		for (Connector connector : connectors) {
			result.add(connectorMapper.toConnectorV211(connector, Instant.now()));
		}
		return result;
	}

	@Named("toStatusV211")
	protected com.duluthtechnologies.ocpi.model.v211.Status toStatusV211(Evse evse) {
		if (evse == null) {
			return null;
		}
		if (evse.getConnectors() == null) {
			return null;
		}
		if (evse.getConnectors().stream().allMatch(c -> c.getStatus() == Connector.Status.AVAILABLE)) {
			return com.duluthtechnologies.ocpi.model.v211.Status.AVAILABLE;
		} else if (evse.getConnectors().stream().allMatch(c -> c.getStatus() == Connector.Status.UNKNOWN)) {
			return com.duluthtechnologies.ocpi.model.v211.Status.UNKNOWN;
		} else {
			return com.duluthtechnologies.ocpi.model.v211.Status.CHARGING;
		}
	}

	public Connector.Status toStatus(com.duluthtechnologies.ocpi.model.v211.EVSE evse) {
		if (evse == null) {
			return null;
		}
		if (evse.status() == com.duluthtechnologies.ocpi.model.v211.Status.AVAILABLE) {
			return Connector.Status.AVAILABLE;
		} else if (evse.status() == com.duluthtechnologies.ocpi.model.v211.Status.UNKNOWN) {
			return Connector.Status.UNKNOWN;
		} else {
			return Connector.Status.UNAVAILABLE;
		}
	}

	@Mapping(source = "uid", target = "ocpiId")
	@Mapping(source = "evse", target = "connectors", qualifiedByName = "toConnectors")
	public abstract EvseImpl toEvse(EVSE evse);

	@Mapping(source = "uid", target = "ocpiId")
	@Mapping(source = "evse", target = "connectors", qualifiedByName = "toConnectorForms")
	public abstract EvseForm toEvseForm(EVSE evse);

	@Named("toConnectors")
	protected List<Connector> toConnectors(EVSE evse) {
		if (evse == null) {
			return null;
		}
		if (evse.connectors() == null) {
			return null;
		}
		return evse.connectors().stream().map(c -> toConnector(c, evse)).toList();
	}

	protected Connector toConnector(com.duluthtechnologies.ocpi.model.v211.Connector connector, EVSE evse) {
		return connectorMapper.toConnector(connector, evse.status());
	}

	@Named("toConnectorForms")
	protected List<ConnectorForm> toConnectorForms(EVSE evse) {
		if (evse == null) {
			return null;
		}
		if (evse.connectors() == null) {
			return null;
		}
		return evse.connectors().stream().map(c -> toConnectorForm(c, evse)).toList();
	}

	protected ConnectorForm toConnectorForm(com.duluthtechnologies.ocpi.model.v211.Connector connector, EVSE evse) {
		return connectorMapper.toConnectorForm(connector, evse.status());
	}
}
