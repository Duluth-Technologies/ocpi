package com.duluthtechnologies.ocpi.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Status;
import com.duluthtechnologies.ocpi.core.store.ConnectorStore;
import com.duluthtechnologies.ocpi.persistence.entity.ConnectorEntity;
import com.duluthtechnologies.ocpi.persistence.entity.EvseEntity;
import com.duluthtechnologies.ocpi.persistence.jpa.ConnectorJPARepository;
import com.duluthtechnologies.ocpi.persistence.jpa.EvseJPARepository;
import com.duluthtechnologies.ocpi.persistence.mapper.ConnectorEntityMapper;

import jakarta.transaction.Transactional;

@Component
public class ConnectorStoreImpl implements ConnectorStore {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectorStoreImpl.class);

	private final ConnectorJPARepository connectorJPARepository;

	private final EvseJPARepository evseJPARepository;

	private final ConnectorEntityMapper connectorEntityMapper;

	private final AuditingHandler auditingHandler;

	public ConnectorStoreImpl(ConnectorJPARepository connectorJPARepository, EvseJPARepository evseJPARepository,
			ConnectorEntityMapper connectorEntityMapper, AuditingHandler auditingHandler) {
		super();
		this.connectorJPARepository = connectorJPARepository;
		this.evseJPARepository = evseJPARepository;
		this.connectorEntityMapper = connectorEntityMapper;
		this.auditingHandler = auditingHandler;
	}

	@Override
	@Transactional
	public Connector createConnector(String evseKey, Connector connector) {
		EvseEntity evseEntity = evseJPARepository.findByKey(evseKey).orElseThrow(() -> {
			String message = "Cannot create Connector linked to EVSE with key [%s] as no EVSE exists with this key";
			LOG.error(message);
			return new RuntimeException(message);
		});
		ConnectorEntity connectorEntity = connectorEntityMapper.toConnectorEntity(connector, evseEntity);
		connectorEntity = connectorJPARepository.save(connectorEntity);
		if (evseEntity.getConnectors() != null) {
			evseEntity.getConnectors().add(connectorEntity);
		} else {
			evseEntity.setConnectors(new ArrayList<>(List.of(connectorEntity)));
		}
		return connectorEntity;
	}

	@Override
	public void delete(String key) {
		ConnectorEntity connectorEntity = connectorJPARepository.findByKey(key).orElseThrow(() -> {
			String message = "Cannot deleted Connector with key [%s] as no Connector exists with this key";
			LOG.error(message);
			return new RuntimeException(message);
		});
		connectorEntity.setDeleted(true);
		connectorJPARepository.save(connectorEntity);

	}

	@Override
	public Connector update(String key, Connector connector) {
		ConnectorEntity connectorEntity = connectorJPARepository.findByKey(key).orElseThrow(() -> {
			String message = "Cannot update Connector with key [%s] as no Connector exists with this key";
			LOG.error(message);
			return new RuntimeException(message);
		});
		connectorEntityMapper.updateConnectorEntity(connectorEntity, connector);
		return connectorJPARepository.save(connectorEntity);
	}

	@Override
	public Connector patch(String key, Connector connector) {
		ConnectorEntity connectorEntity = connectorJPARepository.findByKey(key).orElseThrow(() -> {
			String message = "Cannot update Connector with key [%s] as no Connector exists with this key";
			LOG.error(message);
			return new RuntimeException(message);
		});
		connectorEntityMapper.patchConnectorEntity(connectorEntity, connector);
		return connectorJPARepository.save(connectorEntity);
	}

	@Override
	public Optional<Connector> findByCountryCodeAndPartyIdAndLocationIdAndEvseId(String countryCode, String partyId,
			String locationId, String evseId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Connector> findByKey(String key) {
		return connectorJPARepository.findByKey(key).map(Connector.class::cast);
	}

	@Override
	@Transactional
	public Connector updateStatus(String key, Status updatedStatus) {
		LOG.debug("Updating status of Connector with key [{}] to [{}]...", key, updatedStatus);
		ConnectorEntity connectorEntity = connectorJPARepository.findByKey(key).orElseThrow(() -> {
			String message = "Cannot update status of Connector with key [%s] as no Connector exists with this key";
			LOG.error(message);
			return new RuntimeException(message);
		});
		connectorEntity.setStatus(updatedStatus);
		// We want to force the update of the lastModifiedDate even is the status has
		// not changed.
		auditingHandler.markModified(connectorEntity);
		return connectorJPARepository.save(connectorEntity);
	}

}
