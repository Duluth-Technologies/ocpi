package com.duluthtechnologies.ocpi.service.mapper;

import java.time.Instant;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Status;
import com.duluthtechnologies.ocpi.service.model.impl.ConnectorImpl;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConnectorMapper {

	public abstract ConnectorImpl toConnector(ConnectorForm connectorForm);

	@Mapping(target = "id", source = "connector.connectorId")
	@Mapping(target = "lastUpdated", source = "lastUpdatedTime")
	@Mapping(target = "amperage", source = "connector.maximumAmperage")
	@Mapping(target = "voltage", source = "connector.maximumVoltage")
	@Mapping(target = "standard", source = "connector.type")
	public abstract com.duluthtechnologies.ocpi.model.v211.Connector toConnectorV211(Connector connector,
			Instant lastUpdatedTime);

	@Mapping(source = "connectorV211.id", target = "connectorId")
	@Mapping(source = "connectorV211.amperage", target = "maximumAmperage")
	@Mapping(source = "connectorV211.voltage", target = "maximumVoltage")
	@Mapping(source = "connectorV211.standard", target = "type")
	public abstract ConnectorImpl toConnector(com.duluthtechnologies.ocpi.model.v211.Connector connectorV211,
			Status status);

	protected Connector.Status toStatus(Status status) {
		if (status == null) {
			return null;
		}
		return switch (status) {
		case AVAILABLE -> Connector.Status.AVAILABLE;
		case BLOCKED -> Connector.Status.UNAVAILABLE;
		case CHARGING -> Connector.Status.UNAVAILABLE;
		case INOPERATIVE -> Connector.Status.UNAVAILABLE;
		case OUTOFORDER -> Connector.Status.UNAVAILABLE;
		case PLANNED -> Connector.Status.UNAVAILABLE;
		case REMOVED -> Connector.Status.UNAVAILABLE;
		case RESERVED -> Connector.Status.UNAVAILABLE;
		case UNKNOWN -> Connector.Status.UNKNOWN;
		};
	}

	public EVSE toEvsePatchStatus(com.duluthtechnologies.ocpi.core.model.Evse evse) {
		if (evse == null) {
			return null;
		}
		Status status = toEvseStatus(evse);
		return new EVSE(null, null, status, null, null, null, null, null, null, null, null, null, null);
	}

	private Status toEvseStatus(com.duluthtechnologies.ocpi.core.model.Evse evse) {
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

	@Mapping(source = "connectorV211.id", target = "connectorId")
	@Mapping(source = "connectorV211.amperage", target = "maximumAmperage")
	@Mapping(source = "connectorV211.voltage", target = "maximumVoltage")
	@Mapping(source = "connectorV211.standard", target = "type")
	protected abstract ConnectorForm toConnectorForm(com.duluthtechnologies.ocpi.model.v211.Connector connectorV211,
			Status status);

}
