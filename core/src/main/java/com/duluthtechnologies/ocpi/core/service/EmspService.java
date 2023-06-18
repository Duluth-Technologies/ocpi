package com.duluthtechnologies.ocpi.core.service;

import com.duluthtechnologies.ocpi.core.model.Location;

public interface EmspService {

	Location getLocation(String emspKey, String locationKey);

}
