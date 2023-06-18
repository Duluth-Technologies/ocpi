package com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConnectorV211Mapper {

	@Mapping(target = "maximumAmperage", source = "connector.amperage")
	@Mapping(target = "maximumVoltage", source = "connector.voltage")
	@Mapping(target = "connectorId", source = "connector.id")
	@Mapping(target = "type", source = "connector.standard")
	public abstract ConnectorForm toConnectorForm(com.duluthtechnologies.ocpi.model.v211.Connector connector,
			com.duluthtechnologies.ocpi.model.v211.Status status);

	@Mapping(target = "maximumAmperage", source = "connector.amperage")
	@Mapping(target = "maximumVoltage", source = "connector.voltage")
	@Mapping(target = "connectorId", source = "connector.id")
	@Mapping(target = "type", source = "connector.standard")
	public abstract ConnectorForm toConnectorForm(com.duluthtechnologies.ocpi.model.v211.Connector connector,
			Connector.Status status);

	@Mapping(target = "id", source = "connectorId")
	@Mapping(target = "standard", source = "type")
	@Mapping(target = "amperage", source = "maximumAmperage")
	@Mapping(target = "voltage", source = "maximumVoltage")
	@Mapping(target = "lastUpdated", source = "lastModifiedDate")
	public abstract com.duluthtechnologies.ocpi.model.v211.Connector toConnector(Connector connector);

	protected Connector.Status toStatus(com.duluthtechnologies.ocpi.model.v211.Status status) {
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

}
