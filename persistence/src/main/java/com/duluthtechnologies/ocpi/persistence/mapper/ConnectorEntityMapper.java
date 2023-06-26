package com.duluthtechnologies.ocpi.persistence.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.persistence.entity.ConnectorEntity;
import com.duluthtechnologies.ocpi.persistence.entity.EvseEntity;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface ConnectorEntityMapper {

	@Mapping(target = "evse", source = "evseEntity")
	@Mapping(target = "key", source = "connector.key")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	ConnectorEntity toConnectorEntity(Connector connector, EvseEntity evseEntity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "key", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "evse", ignore = true)
	void updateConnectorEntity(@MappingTarget ConnectorEntity connectorEntity, Connector connectors);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "key", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "evse", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
	void patchConnectorEntity(@MappingTarget ConnectorEntity connectorEntity, Connector connectors);
}
