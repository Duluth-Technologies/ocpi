package com.duluthtechnologies.ocpi.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import com.duluthtechnologies.ocpi.api.dto.CPOLocationView;
import com.duluthtechnologies.ocpi.api.dto.LocationView;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.service.LocationService.LocationForm;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = EvseDTOMapper.class)
public interface LocationDTOMapper {

	LocationForm toLocationForm(com.duluthtechnologies.ocpi.api.dto.LocationCreationForm locationCreationForm);

	@SubclassMapping(source = RegisteredCPOLocation.class, target = CPOLocationView.class)
	LocationView toLocationView(Location location);

	@Mapping(target = "cpoKey", source = "registeredCPO.key")
	CPOLocationView toCpoLocationView(RegisteredCPOLocation registeredCPOLocation);
}
