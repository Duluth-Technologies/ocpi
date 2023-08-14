package com.duluthtechnologies.ocpi.persistence.impl;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.store.LocationStore;
import com.duluthtechnologies.ocpi.persistence.entity.CPOLocationEntity;
import com.duluthtechnologies.ocpi.persistence.entity.LocationEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredCPOEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredOperatorEntity;
import com.duluthtechnologies.ocpi.persistence.jpa.CPOLocationJPARepository;
import com.duluthtechnologies.ocpi.persistence.jpa.LocationJPARepository;
import com.duluthtechnologies.ocpi.persistence.jpa.RegisteredOperatorJPARepository;
import com.duluthtechnologies.ocpi.persistence.mapper.LocationEntityMapper;

import jakarta.transaction.Transactional;

@Component
public class LocationStoreImpl implements LocationStore {

	private static final Logger LOG = LoggerFactory.getLogger(LocationStoreImpl.class);

	private final LocationJPARepository locationJPARepository;

	private final CPOLocationJPARepository cpoLocationJPARepository;

	private final RegisteredOperatorJPARepository registeredOperatorJPARepository;

	private final LocationEntityMapper locationEntityMapper;

	public LocationStoreImpl(LocationJPARepository locationJPARepository, LocationEntityMapper locationEntityMapper,
			RegisteredOperatorJPARepository registeredOperatorJPARepository,
			CPOLocationJPARepository cpoLocationJPARepository) {
		super();
		this.locationJPARepository = locationJPARepository;
		this.cpoLocationJPARepository = cpoLocationJPARepository;
		this.registeredOperatorJPARepository = registeredOperatorJPARepository;
		this.locationEntityMapper = locationEntityMapper;
	}

	@Override
	@Transactional
	public Location createLocation(Location location) {
		LocationEntity locationEntity = locationEntityMapper.toLocationEntity(location);
		return locationJPARepository.save(locationEntity);
	}

	@Override
	public Location getByKey(String key) {
		return locationJPARepository.findByKey(key).orElseThrow(() -> {
			String message = "Cannot find Locaiton with key [%s]".formatted(key);
			LOG.error(message);
			return new RuntimeException(message);
		});
	}

	@Override
	public RegisteredCPOLocation createRegisteredCPOLocation(RegisteredCPOLocation location, String registeredCPOKey) {
		RegisteredOperatorEntity registeredOperatorEntity = registeredOperatorJPARepository.findByKey(registeredCPOKey)
				.orElseThrow(() -> {
					String message = "Cannot create RegisteredCPOLocation for RegisteredCPO with key [%s] as it cannot be found."
							.formatted(registeredCPOKey);
					LOG.error(message);
					return new RuntimeException(message);
				});
		if (!(registeredOperatorEntity instanceof RegisteredCPO)) {
			String message = "Cannot create RegisteredCPOLocation for Registered Operator with key [%s] as it canno."
					.formatted(registeredCPOKey);
			LOG.error(message);
			throw new RuntimeException(message);
		}
		CPOLocationEntity cpoLocationEntity = (CPOLocationEntity) locationEntityMapper.toLocationEntity(location);
		cpoLocationEntity.setRegisteredCPO((RegisteredCPOEntity) registeredOperatorEntity);
		return locationJPARepository.save(cpoLocationEntity);
	}

	@Override
	public RegisteredCPOLocation updateRegisteredCPOLocation(RegisteredCPOLocation registeredCPOLocation) {
		CPOLocationEntity cpoLocationEntity = (CPOLocationEntity) locationJPARepository
				.findByKey(registeredCPOLocation.getKey()).orElseThrow(() -> {
					String message = "Cannot update RegisteredCPOLocation with key [%s] as it cannot be found."
							.formatted(registeredCPOLocation.getKey());
					LOG.error(message);
					return new RuntimeException(message);
				});
		locationEntityMapper.updateCPOLocationEntity(cpoLocationEntity, registeredCPOLocation);
		return locationJPARepository.save(cpoLocationEntity);
	}

	@Override
	public RegisteredCPOLocation patchRegisteredCPOLocation(RegisteredCPOLocation registeredCPOLocation) {
		CPOLocationEntity cpoLocationEntity = (CPOLocationEntity) locationJPARepository
				.findByKey(registeredCPOLocation.getKey()).orElseThrow(() -> {
					String message = "Cannot patch RegisteredCPOLocation with key [%s] as it cannot be found."
							.formatted(registeredCPOLocation.getKey());
					LOG.error(message);
					return new RuntimeException(message);
				});
		locationEntityMapper.patchCPOLocationEntity(cpoLocationEntity, registeredCPOLocation);
		return locationJPARepository.save(cpoLocationEntity);
	}

	@Override
	public Optional<RegisteredCPOLocation> findByCountryCodeAndPartyIdAndOcpiId(String countryCode, String partyId,
			String ocpiId) {
		return locationJPARepository
				.findByRegisteredCpoCountryCodeAndRegisteredCpoPartyIdAndOcpiId(countryCode, partyId, ocpiId)
				.map(RegisteredCPOLocation.class::cast);
	}

	@Override
	public List<RegisteredCPOLocation> findByRegisteredCpoKey(String key) {
		return locationJPARepository.findByRegisteredCpoKey(key).stream().map(RegisteredCPOLocation.class::cast)
				.toList();
	}

	@Override
	public List<Location> findByOcpiId(String ocpiId) {
		return locationJPARepository.findByOcpiId(ocpiId);
	}

	@Override
	public Page<Location> findNotRegisteredLocations(Instant dateFrom, Instant dateTo, Integer offset, Integer limit) {
		// We filter on locations which are not RegisteredCPOLocation to have the own
		// Locations
		Stream<LocationEntity> locationEntities = locationJPARepository.findAll().stream()
				.filter(l -> !(l instanceof CPOLocationEntity));
		if (dateFrom != null) {
			locationEntities = locationEntities.filter(l -> l.getLastModifiedDate().isAfter(dateFrom)
					|| l.getEvses().stream().anyMatch(e -> e.getLastModifiedDate().isAfter(dateFrom))
					|| l.getEvses().stream().flatMap(e -> e.getConnectors().stream())
							.anyMatch(c -> c.getLastModifiedDate().isBefore(dateFrom)));
		}
		if (dateTo != null) {
			locationEntities = locationEntities.filter(l -> l.getLastModifiedDate().isBefore(dateTo)
					|| l.getEvses().stream().anyMatch(e -> e.getLastModifiedDate().isBefore(dateTo))
					|| l.getEvses().stream().flatMap(e -> e.getConnectors().stream())
							.anyMatch(c -> c.getLastModifiedDate().isBefore(dateTo)));
		}
		List<LocationEntity> filteredLocationEntities = locationEntities.toList();
		return new Page(filteredLocationEntities.stream().sorted(Comparator.comparing(LocationEntity::getId))
				.skip(offset).limit(limit).map(Location.class::cast).toList(), filteredLocationEntities.size());
	}

	@Override
	public Optional<RegisteredCPOLocation> findByRegisteredCpoKeyAndOcpiId(String registeredCpoKey,
			String locationOcpiId) {
		return cpoLocationJPARepository.findByRegisteredCPOKeyAndOcpiId(registeredCpoKey, locationOcpiId)
				.map(RegisteredCPOLocation.class::cast);
	}

}
