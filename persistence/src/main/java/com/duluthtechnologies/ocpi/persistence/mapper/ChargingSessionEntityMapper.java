package com.duluthtechnologies.ocpi.persistence.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;
import com.duluthtechnologies.ocpi.persistence.entity.ChargingSessionEntity;
import com.duluthtechnologies.ocpi.persistence.entity.ConnectorEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPChargingSessionEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPEntity;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface ChargingSessionEntityMapper {

	@Mapping(target = "connector", source = "connectorEntity")
	@Mapping(target = "key", source = "chargingSession.key")
	ChargingSessionEntity toChargingSessionEntity(ChargingSession chargingSession, ConnectorEntity connectorEntity);

	@Mapping(target = "connector", source = "connectorEntity")
	@Mapping(target = "registeredEMSP", source = "registeredEMSPEntity")
	@Mapping(target = "key", source = "chargingSession.key")
	RegisteredEMSPChargingSessionEntity toRegisteredEMSPChargingSessionEntity(
			RegisteredEMSPChargingSession chargingSession, ConnectorEntity connectorEntity,
			RegisteredEMSPEntity registeredEMSPEntity);

	@Mapping(target = "key", ignore = true)
	@Mapping(target = "connector", ignore = true)
	void updateChargingSessionEntity(@MappingTarget ChargingSessionEntity chargingSessionEntity,
			ChargingSession chargingSession);

	@Mapping(target = "key", ignore = true)
	@Mapping(target = "connector", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
	void patchChargingSessionEntity(@MappingTarget ChargingSessionEntity chargingSessionEntity,
			ChargingSession chargingSession);

}
