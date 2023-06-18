package com.duluthtechnologies.ocpi.persistence.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.persistence.entity.CPOLocationEntity;
import com.duluthtechnologies.ocpi.persistence.entity.LocationEntity;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface LocationEntityMapper {

	@Mapping(target = "evses", ignore = true)
	@Mapping(target = "id", ignore = true)
	@SubclassMapping(target = CPOLocationEntity.class, source = RegisteredCPOLocation.class)
	LocationEntity toLocationEntity(Location location);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "registeredCPO", ignore = true)
	@Mapping(target = "evses", ignore = true)
	void updateCPOLocationEntity(@MappingTarget CPOLocationEntity cpoLocationEntity,
			RegisteredCPOLocation registeredCPOLocation);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "registeredCPO", ignore = true)
	@Mapping(target = "evses", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
	void patchCPOLocationEntity(@MappingTarget CPOLocationEntity cpoLocationEntity,
			RegisteredCPOLocation registeredCPOLocation);

}
