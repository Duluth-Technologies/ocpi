package com.duluthtechnologies.ocpi.persistence.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.persistence.entity.EvseEntity;
import com.duluthtechnologies.ocpi.persistence.entity.LocationEntity;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface EvseEntityMapper {

	@Mapping(target = "connectors", ignore = true)
	@Mapping(target = "ocpiId", source = "evse.ocpiId")
	@Mapping(target = "key", source = "evse.key")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	@Mapping(target = "location", source = "locationEntity")
	EvseEntity toEvseEntity(Evse evse, LocationEntity locationEntity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "key", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "connectors", ignore = true)
	@Mapping(target = "location", ignore = true)
	void updateEvseEntity(@MappingTarget EvseEntity evseEntity, Evse evse);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "key", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "connectors", ignore = true)
	@Mapping(target = "location", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
	void patchEvseEntity(@MappingTarget EvseEntity evseEntity, Evse evse);
}
