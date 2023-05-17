package com.duluthtechnologies.ocpi.persistence.impl;

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
import jakarta.transaction.Transactional.TxType;

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
		return doUpdate(registeredOperator);
	}

	@Override
	@Transactional(value = TxType.REQUIRES_NEW)
	public RegisteredOperator updateNow(RegisteredOperator registeredOperator) {
		return doUpdate(registeredOperator);
	}

	private RegisteredOperator doUpdate(RegisteredOperator registeredOperator) {
		RegisteredOperatorEntity registeredOperatorEntity = registeredOperatorJPARepository
				.findByKey(registeredOperator.getKey()).orElseThrow(() -> {
					String message = "Cannot update RegisteredOperator with key [%s] as it cannot be found."
							.formatted(registeredOperator.getKey());
					LOG.error(message);
					throw new RuntimeException(message);
				});
		registeredOperatorJPARepository.delete(registeredOperatorEntity);
		entityManager.flush();
		RegisteredOperatorEntity entity;
		if (registeredOperator instanceof RegisteredCPOV211 registeredCPOV211) {
			entity = registeredOperatorMapper.toEntity(registeredCPOV211, registeredOperatorEntity.getId(),
					registeredOperatorEntity.getCreatedDate());
		} else if (registeredOperator instanceof RegisteredEMSPV211 registeredEMSPV211) {
			entity = registeredOperatorMapper.toEntity(registeredEMSPV211, registeredOperatorEntity.getId(),
					registeredOperatorEntity.getCreatedDate());
		} else if (registeredOperator instanceof RegisteredCPO registeredCPO) {
			entity = registeredOperatorMapper.toEntity(registeredCPO, registeredOperatorEntity.getId(),
					registeredOperatorEntity.getCreatedDate());
		} else if (registeredOperator instanceof RegisteredEMSP registeredEMSP) {
			entity = registeredOperatorMapper.toEntity(registeredEMSP, registeredOperatorEntity.getId(),
					registeredOperatorEntity.getCreatedDate());
		} else {
			throw new IllegalArgumentException(
					"Not all subclasses are supported for this mapping. Missing for " + registeredOperator.getClass());
		}
		return registeredOperatorJPARepository.save(entity);
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

}
