package com.duluthtechnologies.ocpi.service.security.filter;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.store.LocationStore;
import com.duluthtechnologies.ocpi.service.security.SecurityContextFilter;

@Component
public class LocationKeyRegisteredCPOFilter implements SecurityContextFilter<String> {

	private final LocationStore locationStore;

	public LocationKeyRegisteredCPOFilter(LocationStore locationStore) {
		super();
		this.locationStore = locationStore;
	}

	@Override
	public boolean filter(String locationKey) {
		RegisteredCPOLocation registeredCPOLocation = (RegisteredCPOLocation) locationStore.getByKey(locationKey);
		return Objects.equals(registeredCPOLocation.getRegisteredCPO().getKey(), SecurityContext.getCPOKey());
	}

}
