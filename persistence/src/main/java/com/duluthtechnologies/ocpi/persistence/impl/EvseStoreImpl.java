package com.duluthtechnologies.ocpi.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.store.EvseStore;
import com.duluthtechnologies.ocpi.persistence.entity.EvseEntity;
import com.duluthtechnologies.ocpi.persistence.entity.LocationEntity;
import com.duluthtechnologies.ocpi.persistence.jpa.EvseJPARepository;
import com.duluthtechnologies.ocpi.persistence.jpa.LocationJPARepository;
import com.duluthtechnologies.ocpi.persistence.mapper.EvseEntityMapper;

import jakarta.transaction.Transactional;

@Component
public class EvseStoreImpl implements EvseStore {

	private static final Logger LOG = LoggerFactory.getLogger(EvseStoreImpl.class);

	private final EvseJPARepository evseJPARepository;

	private final LocationJPARepository locationJPARepository;

	private final EvseEntityMapper evseEntityMapper;

	public EvseStoreImpl(EvseJPARepository evseJPARepository, EvseEntityMapper evseEntityMapper,
			LocationJPARepository locationJPARepository) {
		super();
		this.evseJPARepository = evseJPARepository;
		this.locationJPARepository = locationJPARepository;
		this.evseEntityMapper = evseEntityMapper;
	}

	@Override
	@Transactional
	public Evse createEVSE(String locationKey, Evse evse) {
		LocationEntity locationEntity = locationJPARepository.findByKey(locationKey).orElseThrow(() -> {
			String message = "Cannot create EVSE linked to Location with key [%s] as no Location exists with this key"
					.formatted(locationKey);
			LOG.error(message);
			return new RuntimeException(message);
		});
		EvseEntity evseEntity = evseEntityMapper.toEvseEntity(evse, locationEntity);
		evseEntity = evseJPARepository.save(evseEntity);
		if (locationEntity.getEvses() != null) {
			locationEntity.getEvses().add(evseEntity);
		} else {
			locationEntity.setEvses(new ArrayList<>(List.of(evseEntity)));
		}
		return evseEntity;
	}

	@Override
	@Transactional
	public Evse updateEVSE(String evseKey, Evse evse) {
		EvseEntity evseEntity = evseJPARepository.findByKey(evseKey).orElseThrow(() -> {
			String message = "Cannot update EVSE with key [%s] as no EVSE exists with this key".formatted(evseKey);
			LOG.error(message);
			return new RuntimeException(message);
		});
		evseEntityMapper.updateEvseEntity(evseEntity, evse);
		return evseJPARepository.save(evseEntity);
	}

	@Override
	public Evse patchEVSE(String key, Evse evse) {
		EvseEntity evseEntity = evseJPARepository.findByKey(key).orElseThrow(() -> {
			String message = "Cannot patch EVSE with key [%s] as no EVSE exists with this key".formatted(key);
			LOG.error(message);
			return new RuntimeException(message);
		});
		evseEntityMapper.patchEvseEntity(evseEntity, evse);
		return evseJPARepository.save(evseEntity);
	}

	@Override
	@Transactional
	public void delete(String key) {
		EvseEntity evseEntity = evseJPARepository.findByKey(key).orElseThrow(() -> {
			String message = "Cannot delete EVSE with key [%s] as no EVSE exists with this key".formatted(key);
			LOG.error(message);
			return new RuntimeException(message);
		});
		evseEntity.setDeleted(true);
		evseJPARepository.save(evseEntity);
	}

	@Override
	public Optional<Evse> findByCountryCodeAndPartyIdAndLocationOcpiIdAndEvseOcpiId(String countryCode, String partyId,
			String locationOcpiId, String evseOcpiId) {
		return evseJPARepository
				.findByCPOLocationRegisteredOperatorCountryCodeAndCPOLocationRegisteredOperatorPartyIdAndCPOLocationOcpiIdAndOcpiId(
						countryCode, partyId, locationOcpiId, evseOcpiId)
				.map(Evse.class::cast);
	}

	@Override
	public Optional<Evse> findByKey(String key) {
		return evseJPARepository.findByKey(key).map(Evse.class::cast);
	}

}
