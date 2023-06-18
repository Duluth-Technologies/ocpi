package com.duluthtechnologies.ocpi.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.api.dto.ConnectorView;
import com.duluthtechnologies.ocpi.core.model.Connector;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface ConnectorDTOMapper {

	ConnectorView toConnectorView(Connector connector);

}
