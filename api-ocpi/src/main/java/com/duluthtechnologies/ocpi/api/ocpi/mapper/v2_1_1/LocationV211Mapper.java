package com.duluthtechnologies.ocpi.api.ocpi.mapper.v2_1_1;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.service.LocationService.EvseForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.LocationForm;
import com.duluthtechnologies.ocpi.model.v211.EVSE;
import com.duluthtechnologies.ocpi.model.v211.Location;
import com.duluthtechnologies.ocpi.model.v211.Status;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = EvseV211Mapper.class)
public abstract class LocationV211Mapper {

	@Autowired
	protected EvseV211Mapper evseMapper;

	@Mapping(target = "ocpiId", source = "id")
	@Mapping(target = "zipCode", source = "postalCode")
	@Mapping(target = "countryCode", source = "country")
	@Mapping(target = "latitude", source = "coordinates.latitude")
	@Mapping(target = "longitude", source = "coordinates.longitude")
	public abstract LocationForm toLocationForm(Location location);

	protected List<EvseForm> toEvses(List<EVSE> evses) {
		if (evses == null) {
			return null;
		}
		return evses.stream().filter(evse -> evse.status() != Status.REMOVED).map(evseMapper::toEvseForm).toList();
	}

	@Mapping(source = "ocpiId", target = "id")
	@Mapping(source = "zipCode", target = "postalCode")
	@Mapping(source = "countryCode", target = "country")
	@Mapping(source = "latitude", target = "coordinates.latitude")
	@Mapping(source = "longitude", target = "coordinates.longitude")
	public abstract Location toLocation(com.duluthtechnologies.ocpi.core.model.Location location);

	public abstract List<Location> toLocations(
			List<? extends com.duluthtechnologies.ocpi.core.model.Location> locations);

}
