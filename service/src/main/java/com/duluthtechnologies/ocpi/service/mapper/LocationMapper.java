package com.duluthtechnologies.ocpi.service.mapper;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.service.LocationService.LocationForm;
import com.duluthtechnologies.ocpi.service.model.impl.LocationImpl;
import com.duluthtechnologies.ocpi.service.model.impl.RegisteredCPOLocationImpl;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = EvseMapper.class)
public abstract class LocationMapper {

	private static final DecimalFormat df = new DecimalFormat("0.000000");

	@Autowired
	protected EvseMapper evseMapper;

	@Mapping(target = "evses", ignore = true)
	public abstract LocationImpl toLocation(LocationForm locationForm);

	@Mapping(target = "evses", ignore = true)
	@Mapping(target = "registeredCPO", ignore = true)
	public abstract RegisteredCPOLocationImpl toRegisteredCPOLocation(LocationForm locationForm);

	@Mapping(target = "id", source = "location.ocpiId")
	@Mapping(target = "postalCode", source = "location.zipCode")
	@Mapping(target = "country", source = "location.countryCode")
	@Mapping(target = "lastUpdated", source = "lastUpdatedTime")
	@Mapping(target = "coordinates.latitude", source = "location.latitude", qualifiedByName = "toCoordinateV211")
	@Mapping(target = "coordinates.longitude", source = "location.longitude", qualifiedByName = "toCoordinateV211")
	@Mapping(target = "evses", source = "location.evses", qualifiedByName = "toEvsesV211")
	@Mapping(target = "type", constant = "UNKNOWN")
	public abstract com.duluthtechnologies.ocpi.model.v211.Location toLocationV211(Location location,
			Instant lastUpdatedTime);

	@Named("toCoordinateV211")
	protected String toCoordinate(double coordinate) {
		return df.format(coordinate);
	}

	@Named("fromCoordinateV211")
	protected Double fromCoordinate(String coordinate) {
		return Double.parseDouble(coordinate);
	}

	@Named("toEvsesV211")
	protected List<com.duluthtechnologies.ocpi.model.v211.EVSE> toEvsesV211(List<Evse> evses) {
		if (evses == null) {
			return null;
		}
		List<com.duluthtechnologies.ocpi.model.v211.EVSE> result = new ArrayList<>();
		for (Evse evse : evses) {
			result.add(evseMapper.toEvseV211(evse, Instant.now()));
		}
		return result;
	}

	@Mapping(source = "locationV211.id", target = "ocpiId")
	@Mapping(source = "locationV211.postalCode", target = "zipCode")
	@Mapping(source = "locationV211.country", target = "countryCode")
	@Mapping(source = "locationV211.lastUpdated", target = "lastModifiedDate")
	@Mapping(source = "locationV211.coordinates.latitude", target = "latitude", qualifiedByName = "fromCoordinateV211")
	@Mapping(source = "locationV211.coordinates.longitude", target = "longitude", qualifiedByName = "fromCoordinateV211")
	public abstract LocationImpl toLocation(com.duluthtechnologies.ocpi.model.v211.Location locationV211, String key);

	@Mapping(source = "id", target = "ocpiId")
	@Mapping(source = "postalCode", target = "zipCode")
	@Mapping(source = "country", target = "countryCode")
	@Mapping(source = "coordinates.latitude", target = "latitude", qualifiedByName = "fromCoordinateV211")
	@Mapping(source = "coordinates.longitude", target = "longitude", qualifiedByName = "fromCoordinateV211")
	public abstract LocationForm toLocationForm(com.duluthtechnologies.ocpi.model.v211.Location locationV211);
}
