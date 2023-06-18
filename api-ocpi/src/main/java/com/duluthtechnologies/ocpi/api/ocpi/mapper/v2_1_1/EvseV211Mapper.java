package com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.EvseForm;
import com.duluthtechnologies.ocpi.model.v211.Connector;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Status;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = ConnectorV211Mapper.class)
public abstract class EvseV211Mapper {

	@Autowired
	protected ConnectorV211Mapper connectorV211Mapper;

	@Mapping(target = "ocpiId", source = "uid")
	@Mapping(target = "connectors", source = "evse", qualifiedByName = "toConnectorForms")
	public abstract EvseForm toEvseForm(EVSE evse);

	@Mapping(source = "ocpiId", target = "uid")
	@Mapping(source = "evse", target = "status", qualifiedByName = "toEvseStatus")
	public abstract EVSE toEvse(com.duluthtechnologies.ocpi.core.model.Evse evse);

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

	protected ConnectorForm toConnectorForm(Connector connector, EVSE evse) {
		return connectorV211Mapper.toConnectorForm(connector, evse.status());
	}

	@Named("toEvseStatus")
	protected Status toEvseStatus(com.duluthtechnologies.ocpi.core.model.Evse evse) {
		if (evse == null) {
			return null;
		}
		if (evse.getConnectors() == null) {
			return null;
		}
		if (evse.getConnectors().stream().allMatch(
				c -> c.getStatus().equals(com.duluthtechnologies.ocpi.core.model.Connector.Status.AVAILABLE))) {
			return Status.AVAILABLE;
		} else if (evse.getConnectors().stream()
				.allMatch(c -> c.getStatus().equals(com.duluthtechnologies.ocpi.core.model.Connector.Status.UNKNOWN))) {
			return Status.UNKNOWN;
		} else {
			return Status.CHARGING;
		}
	}

}
