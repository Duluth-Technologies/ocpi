package com.duluthtechnologies.ocpi.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.api.client.OcpiApiClient;
import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.EvseService;
import com.duluthtechnologies.ocpi.core.service.LocationService;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.core.store.ConnectorStore;
import com.duluthtechnologies.ocpi.core.store.EvseStore;
import com.duluthtechnologies.ocpi.core.store.LocationStore;
import com.duluthtechnologies.ocpi.service.helper.KeyGenerator;
import com.duluthtechnologies.ocpi.service.mapper.LocationMapper;
import com.duluthtechnologies.ocpi.service.model.impl.LocationImpl;
import com.duluthtechnologies.ocpi.service.model.impl.RegisteredCPOLocationImpl;
import com.duluthtechnologies.ocpi.service.security.ApplySecurityFiltering;
import com.duluthtechnologies.ocpi.service.security.SecurityContextFiltered;
import com.duluthtechnologies.ocpi.service.security.filter.LocationKeyRegisteredCPOFilter;
import com.duluthtechnologies.ocpi.service.security.filter.RegisteredOperatorKeyFilter;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;

@Component
public class LocationServiceImpl implements LocationService {

	private static final Logger LOG = LoggerFactory.getLogger(LocationServiceImpl.class);

	private final LocationStore locationStore;

	private final LocationMapper locationMapper;

	private final EvseService evseService;

	private final EvseStore evseStore;

	private final ConnectorStore connectorStore;

	private final RegisteredOperatorService registeredOperatorService;

	private final Optional<CPOInfo> cpoInfo;

	private final OcpiApiClient ocpiApiClient;

	private final TaskExecutor taskExecutor;

	public LocationServiceImpl(LocationStore locationStore, EvseStore evseStore, ConnectorStore connectorStore,
			LocationMapper locationMapper, RegisteredOperatorService registeredOperatorService, EvseService evseService,
			Optional<CPOInfo> cpoInfo, @Qualifier("service-task-executor") TaskExecutor taskExecutor,
			OcpiApiClient ocpiApiClient) {
		super();
		this.locationStore = locationStore;
		this.locationMapper = locationMapper;
		this.evseService = evseService;
		this.evseStore = evseStore;
		this.connectorStore = connectorStore;
		this.registeredOperatorService = registeredOperatorService;
		this.cpoInfo = cpoInfo;
		this.ocpiApiClient = ocpiApiClient;
		this.taskExecutor = taskExecutor;
	}

	@Transactional
	public void synchronizeWithRegisteredCpo(String registeredCPOKey) {
		LOG.debug("Synchronizing with Registered CPO with key [{}]...", registeredCPOKey);
		try {
			RegisteredCPO registeredCPO = registeredOperatorService.findCPOByKey(registeredCPOKey).get();
			if (registeredCPO instanceof RegisteredCPOV211 registeredCPOV211) {
				if (registeredCPOV211.getLocationsUrl() == null) {
					LOG.warn(
							"It is not possible to synchronize Locations with registered CPO with key [{}] as it doesn't have any Location URL.",
							registeredCPO.getKey());
					return;
				}
				List<com.duluthtechnologies.ocpi.model.v211.Location> locationV211s = ocpiApiClient
						.getLocationsV211(registeredCPOV211.getOutgoingToken(), registeredCPOV211.getLocationsUrl());
				LOG.debug("Found [{}] Locations on registered CPO with key [{}].", locationV211s.size(),
						registeredCPOV211.getKey());
				for (com.duluthtechnologies.ocpi.model.v211.Location locationV211 : locationV211s) {
					Optional<RegisteredCPOLocation> registeredCPOLocationOptional = locationStore
							.findByCountryCodeAndPartyIdAndOcpiId(registeredCPO.getCountryCode(),
									registeredCPO.getPartyId(), locationV211.id());
					LocationForm locationForm = locationMapper.toLocationForm(locationV211);
					if (registeredCPOLocationOptional.isEmpty()) {
						createRegisteredCPOLocation(locationForm, registeredCPO.getKey());
					} else {
						updateRegisteredCPOLocation(registeredCPOLocationOptional.get().getKey(), locationForm);
					}
				}
			} else {
				LOG.warn("Registered CPO with key [{}] is of type [{}] which is not handled.", registeredCPO.getKey(),
						registeredCPO.getClass().getSimpleName());
			}
		} catch (Exception e) {
			String message = "Exception caught while synchronizing with registered CPO with key [%s]"
					.formatted(registeredCPOKey);
			LOG.error(message);
			throw new RuntimeException(message, e);
		}
	}

	@Override
	@Transactional
	public Location createLocation(LocationForm locationForm) {
		LOG.info("Creating Location [{}]...", locationForm);

		// Mapping to Location object model
		LocationImpl locationImpl = locationMapper.toLocation(locationForm);

		// Generate key
		locationImpl.setKey(KeyGenerator.generateKey(Location.class));
		LOG.debug("Location key is [{}].", locationImpl.getKey());

		// If no OCPI id has been provided, generate automatically one
		if (locationImpl.getOcpiId() == null) {
			locationImpl.setOcpiId(UUID.randomUUID().toString());
		}
		LOG.debug("OCPI id for Location with key [{}] is [{}].", locationImpl.getKey(), locationImpl.getOcpiId());

		// Create Location object
		Location location = locationStore.createLocation(locationImpl);

		// Create EVSEs
		for (EvseForm evseForm : locationForm.evses()) {
			evseService.create(location.getKey(), evseForm);
		}
		// Retrieve Location after it has been saved
		location = locationStore.getByKey(location.getKey());

		pushLocationOnEMSPs(location);
		return location;
	}

	private void pushLocationOnEMSPs(Location location) {
		Instant locationLastUpdatedTime = lastUpdatedTime(location);
		List<RegisteredEMSP> registeredEMSPs = registeredOperatorService.findEMSPs();
		LOG.info("Publishing asynchronously Location with key [{}] on EMSPs with key {}...", location.getKey(),
				registeredEMSPs.stream().map(RegisteredEMSP::getKey).toList());
		taskExecutor.execute(() -> {
			for (RegisteredEMSP registeredEMSP : registeredEMSPs) {
				LOG.debug("Publishing Location with key [{}] on EMSP with key [{}]...", location.getKey(),
						registeredEMSP.getKey());
				try {
					if (registeredEMSP instanceof RegisteredEMSPV211 registeredEMSPV211) {
						if (registeredEMSPV211.getOutgoingToken() != null
								&& registeredEMSPV211.getLocationsUrl() != null) {
							com.duluthtechnologies.ocpi.model.v211.Location locationV211 = locationMapper
									.toLocationV211(location, locationLastUpdatedTime);
							ocpiApiClient.putLocationV211(registeredEMSPV211.getOutgoingToken(),
									registeredEMSPV211.getLocationsUrl(), cpoInfo.get().getCountryCode(),
									cpoInfo.get().getPartyId(), location.getOcpiId(), locationV211);
						}
					}
				} catch (Exception e) {
					String message = "Exception caught while publishing Location with key [%s] on EMSP with key [%s]"
							.formatted(location.getKey(), registeredEMSP.getKey());
					LOG.error(message, e); // No rethrow as we want to continue
				}
			}
		});
	}

	protected Instant lastUpdatedTime(Location location) {
		LOG.debug("Computing last updated time of Location with key [{}]...", location.getKey());
		Instant lastUpdatedTime = location.getLastModifiedDate();
		for (Evse evse : location.getEvses()) {
			if (evse.getLastModifiedDate().isAfter(lastUpdatedTime)) {
				lastUpdatedTime = evse.getLastModifiedDate();
				for (Connector connector : evse.getConnectors()) {
					if (connector.getLastModifiedDate().isAfter(lastUpdatedTime)) {
						lastUpdatedTime = connector.getLastModifiedDate();
					}
				}
			}
		}
		return lastUpdatedTime;
	}

	@Override
	@Transactional
	@ApplySecurityFiltering
	public RegisteredCPOLocation createRegisteredCPOLocation(LocationForm locationForm,
			@SecurityContextFiltered(filter = RegisteredOperatorKeyFilter.class) String registeredCPOKey) {
		LOG.info("Creating Registered CPO Location...");

		// Mapping to RegisteredCPOLocation object model
		RegisteredCPOLocationImpl registeredCPOLocationImpl = locationMapper.toRegisteredCPOLocation(locationForm);

		// Generate key
		registeredCPOLocationImpl.setKey(KeyGenerator.generateKey(Location.class));
		LOG.debug("Location key is [{}].", registeredCPOLocationImpl.getKey());

		// If no OCPI id has been provided, generate automatically one
		if (registeredCPOLocationImpl.getOcpiId() == null) {
			registeredCPOLocationImpl.setOcpiId(UUID.randomUUID().toString());
		}
		LOG.debug("OCPI id for Location with key [{}] is [{}].", registeredCPOLocationImpl.getKey(),
				registeredCPOLocationImpl.getOcpiId());

		// Create Location object
		RegisteredCPOLocation registeredCPOLocation = (RegisteredCPOLocation) locationStore
				.createRegisteredCPOLocation(registeredCPOLocationImpl, registeredCPOKey);

		// Create EVSEs
		for (EvseForm evseForm : locationForm.evses()) {
			evseService.create(registeredCPOLocation.getKey(), evseForm);
		}
		// Retrieve Location after it has been saved
		registeredCPOLocation = (RegisteredCPOLocation) locationStore.getByKey(registeredCPOLocation.getKey());

		return registeredCPOLocation;
	}

	@Override
	@Transactional
	@ApplySecurityFiltering
	public RegisteredCPOLocation updateRegisteredCPOLocation(
			@SecurityContextFiltered(filter = LocationKeyRegisteredCPOFilter.class) String registeredCPOLocationKey,
			LocationForm locationForm) {
		LOG.info("Updating Registered CPO Location with key [{}]...", registeredCPOLocationKey);

		// Mapping to Location object model
		RegisteredCPOLocationImpl registeredCPOLocationImpl = locationMapper.toRegisteredCPOLocation(locationForm);
		registeredCPOLocationImpl.setKey(registeredCPOLocationKey);
		RegisteredCPOLocation registeredCPOLocation = locationStore
				.updateRegisteredCPOLocation(registeredCPOLocationImpl);
		// Create or update EVSEs
		for (EvseForm evseForm : locationForm.evses()) {
			// Check if this evse exists already to decide whether it has to be updated or
			// created
			Optional<Evse> existingEvse = registeredCPOLocation.getEvses().stream()
					.filter(evse -> evse.getOcpiId().equals(evseForm.ocpiId())).findFirst();
			if (existingEvse.isPresent()) {
				evseService.update(existingEvse.get().getKey(), evseForm);
			} else {
				// Create EVSE
				evseService.create(registeredCPOLocationKey, evseForm);
			}
		}
		// Delete EVSEs
		for (Evse evse : registeredCPOLocation.getEvses()) {
			// Check if the evse is still in the form
			if (locationForm.evses().stream().noneMatch(e -> e.ocpiId().equals(evse.getOcpiId()))) {
				// Delete all connectors
				for (Connector connector : evse.getConnectors()) {
					connectorStore.delete(connector.getKey());
				}
				// Delete evse
				evseStore.delete(evse.getKey());
			}
		}
		// Retrieve Location after it has been saved
		registeredCPOLocation = (RegisteredCPOLocation) locationStore.getByKey(registeredCPOLocation.getKey());

		return registeredCPOLocation;

	}

	@Override
	@Transactional
	@ApplySecurityFiltering
	public void patchRegisteredCPOLocation(
			@SecurityContextFiltered(filter = LocationKeyRegisteredCPOFilter.class) String key,
			LocationForm locationForm) {
		RegisteredCPOLocationImpl registeredCPOLocationImpl = locationMapper.toRegisteredCPOLocation(locationForm);
		registeredCPOLocationImpl.setKey(key);
		locationStore.patchRegisteredCPOLocation(registeredCPOLocationImpl);
	}

	@Override
	public Optional<RegisteredCPOLocation> findRegisteredCPOLocation(String countryCode, String partyId,
			String ocpiId) {
		return locationStore.findByCountryCodeAndPartyIdAndOcpiId(countryCode, partyId, ocpiId)
				.map(RegisteredCPOLocation.class::cast);
	}

	@Override
	public List<RegisteredCPOLocation> findByRegisteredCpoKey(String key) {
		return locationStore.findByRegisteredCpoKey(key);
	}

	@Override
	public List<Location> findLocationByOcpiId(String ocpiId) {
		return locationStore.findByOcpiId(ocpiId);
	}

	@Override
	public Page<Location> findLocation(@NotEmpty String countryCode, @NotEmpty String partyId, Instant dateFrom,
			Instant dateTo, Integer offset, Integer limit) {
		if (countryCode.equals(cpoInfo.get().getCountryCode()) && partyId.equals(cpoInfo.get().getPartyId())) {
			return locationStore.findNotRegisteredLocations(dateFrom, dateTo, offset, limit);
		} else {
			throw new UnsupportedOperationException();
		}

	}

}