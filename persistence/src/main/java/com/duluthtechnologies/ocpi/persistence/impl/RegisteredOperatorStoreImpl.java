package com.duluthtechnologies.ocpi.persistence.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.store.RegisteredOperatorStore;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredOperatorEntity;
import com.duluthtechnologies.ocpi.persistence.helper.TokenEncryptor;
import com.duluthtechnologies.ocpi.persistence.jpa.RegisteredOperatorJPARepository;
import com.duluthtechnologies.ocpi.persistence.mapper.RegisteredOperatorEntityMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class RegisteredOperatorStoreImpl implements RegisteredOperatorStore {

	private static final Logger LOG = LoggerFactory.getLogger(RegisteredOperatorStoreImpl.class);

	private final RegisteredOperatorEntityMapper registeredOperatorMapper;

	private final RegisteredOperatorJPARepository registeredOperatorJPARepository;

	private final TokenEncryptor tokenEncryptor;

	@PersistenceContext
	private EntityManager entityManager;

	public RegisteredOperatorStoreImpl(RegisteredOperatorEntityMapper registeredOperatorMapper,
			RegisteredOperatorJPARepository registeredOperatorJPARepository, TokenEncryptor tokenEncryptor) {
		super();
		this.registeredOperatorMapper = registeredOperatorMapper;
		this.registeredOperatorJPARepository = registeredOperatorJPARepository;
		this.tokenEncryptor = tokenEncryptor;
	}

	@Override
	public RegisteredOperator create(RegisteredOperator registeredOperator) {
		RegisteredOperatorEntity entity = registeredOperatorMapper.toEntity(registeredOperator);
		return registeredOperatorJPARepository.save(entity);
	}

	@Override
	@Transactional
	public RegisteredOperator update(RegisteredOperator registeredOperator) {
		RegisteredOperatorEntity registeredOperatorEntity = registeredOperatorJPARepository
				.findByKey(registeredOperator.getKey()).orElseThrow(() -> {
					String message = "Cannot update RegisteredOperator with key [%s] as it cannot be found."
							.formatted(registeredOperator.getKey());
					LOG.error(message);
					throw new RuntimeException(message);
				});
		if (registeredOperatorEntity instanceof RegisteredEMSP
				&& !(registeredOperatorEntity instanceof RegisteredEMSPV211)
				&& (registeredOperator instanceof RegisteredEMSPV211)) {
			// This upgrade will add the data needed in the EMSP v211 table
			registeredOperatorJPARepository.upgradeEMSPToV211(registeredOperatorEntity.getId());
			// Need to detach the entity so that call to find next line will actually etch
			// the inherited entity
			entityManager.detach(registeredOperatorEntity);
			// Fetch again the entity after type change
			registeredOperatorEntity = registeredOperatorJPARepository.findByKey(registeredOperator.getKey()).get();
		} else if (registeredOperatorEntity instanceof RegisteredCPO
				&& !(registeredOperatorEntity instanceof RegisteredCPOV211)
				&& (registeredOperator instanceof RegisteredCPOV211)) {
			registeredOperatorJPARepository.upgradeCPOToV211(registeredOperatorEntity.getId());
			entityManager.detach(registeredOperatorEntity);
			registeredOperatorEntity = registeredOperatorJPARepository.findByKey(registeredOperator.getKey()).get();
		}
		registeredOperatorMapper.updateEntity(registeredOperatorEntity, registeredOperator);
		return registeredOperatorJPARepository.save(registeredOperatorEntity);
	}

	@Override
	public Optional<RegisteredOperator> findByIncomingToken(String token) {
		// TODO Fix for more efficient
		return registeredOperatorJPARepository.findAll().stream().filter(roe -> token.equals(roe.getIncomingToken()))
				.findFirst().map(RegisteredOperator.class::cast);
	}

	@Override
	public Optional<RegisteredOperator> findByKey(String key) {
		return registeredOperatorJPARepository.findByKey(key).map(RegisteredOperator.class::cast);
	}

	@Override
	public List<RegisteredCPO> findCPOs() {
		// TODO Fix for more efficient
		return registeredOperatorJPARepository.findAll().stream().filter(RegisteredCPO.class::isInstance)
				.map(RegisteredCPO.class::cast).toList();
	}

	@Override
	public List<RegisteredEMSP> findEMSPs() {
		// TODO Fix for more efficient
		return registeredOperatorJPARepository.findAll().stream().filter(RegisteredEMSP.class::isInstance)
				.map(RegisteredEMSP.class::cast).toList();
	}

}
