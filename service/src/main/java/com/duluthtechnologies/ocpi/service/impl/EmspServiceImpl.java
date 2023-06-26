package com.duluthtechnologies.ocpi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.EmspService;
import com.duluthtechnologies.ocpi.core.store.LocationStore;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.service.mapper.LocationMapper;

@Component
@ConditionalOnBean(CPOInfo.class)
public class EmspServiceImpl implements EmspService {

	private static final Logger LOG = LoggerFactory.getLogger(EmspServiceImpl.class);

	private final RegisteredOperatorStore registeredOperatorStore;

	private final LocationStore locationStore;

	private final LocationMapper locationMapper;

	private final RestTemplate restTemplate;

	private final CPOInfo cpoInfo;

	public EmspServiceImpl(RegisteredOperatorStore registeredOperatorStore, LocationStore locationStore,
			CPOInfo cpoInfo, LocationMapper locationMapper) {
		super();
		this.registeredOperatorStore = registeredOperatorStore;
		this.locationStore = locationStore;
		this.locationMapper = locationMapper;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		this.cpoInfo = cpoInfo;
	}

	@Override
	public Location getLocation(String emspKey, String locationKey) {
		RegisteredEMSP registeredEMSP = (RegisteredEMSP) registeredOperatorStore.findByKey(emspKey).orElseThrow(() -> {
			String message = "Cannot find Registered EMSP with key [%s]".formatted(emspKey);
			LOG.error(message);
			return new RuntimeException(message);
		});
		// Retrieving the Location to get the OCPI id
		Location location = locationStore.getByKey(locationKey);
		if (registeredEMSP instanceof RegisteredEMSPV211 registeredEMSPV211) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Token " + registeredEMSP.getOutgoingToken());
			HttpEntity entity = new HttpEntity<>(headers);
			com.duluthtechnologies.ocpi.model.v211.Location locationV211 = restTemplate.exchange(
					registeredEMSPV211.getLocationsUrl() + "/" + cpoInfo.getCountryCode() + "/" + cpoInfo.getPartyId()
							+ "/" + location.getOcpiId(),
					HttpMethod.GET, entity,
					new ParameterizedTypeReference<Response<com.duluthtechnologies.ocpi.model.v211.Location>>() {
					}).getBody().data();
			return locationMapper.toLocation(locationV211, locationKey);
		} else {
			String message = "Registered EMSP with key [%s] is of type [%s] which is not supported.";
			LOG.error(message);
			throw new RuntimeException(message);
		}
	}

}
