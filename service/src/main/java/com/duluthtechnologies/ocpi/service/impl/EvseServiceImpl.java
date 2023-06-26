package com.duluthtechnologies.ocpi.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.service.ConnectorService;
import com.duluthtechnologies.ocpi.core.service.EvseService;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;
import com.duluthtechnologies.ocpi.core.service.LocationService.EvseForm;
import com.duluthtechnologies.ocpi.core.store.EvseStore;
import com.duluthtechnologies.ocpi.service.helper.KeyGenerator;
import com.duluthtechnologies.ocpi.service.mapper.EvseMapper;
import com.duluthtechnologies.ocpi.service.model.impl.EvseImpl;

import jakarta.transaction.Transactional;

@Component
public class EvseServiceImpl implements EvseService {

	private static final Logger LOG = LoggerFactory.getLogger(EvseServiceImpl.class);

	private final EvseStore evseStore;

	private final ConnectorService connectorService;

	private final EvseMapper evseMapper;

	public EvseServiceImpl(EvseStore evseStore, EvseMapper evseMapper, ConnectorService connectorService) {
		super();
		this.evseStore = evseStore;
		this.connectorService = connectorService;
		this.evseMapper = evseMapper;
	}

	@Override
	public Optional<Evse> find(String countryCode, String partyId, String locationOcpiId, String evseOcpiId) {
		return evseStore.findByCountryCodeAndPartyIdAndLocationOcpiIdAndEvseOcpiId(countryCode, partyId, locationOcpiId,
				evseOcpiId);
	}

	@Override
	@Transactional
	public Evse create(String locationKey, EvseForm evseForm) {
		LOG.info("Creating EVSE [{}] for Location with key [{}]...", evseForm, locationKey);
		EvseImpl evseImpl = evseMapper.toEvse(evseForm);
		evseImpl.setKey(KeyGenerator.generateKey(Evse.class));
		Evse evse = evseStore.createEVSE(locationKey, evseImpl);
		// Create Connector
		for (ConnectorForm connectorForm : evseForm.connectors()) {
			connectorService.create(evse.getKey(), connectorForm);
		}
		return evseStore.findByKey(evse.getKey()).get();
	}

	@Override
	public Evse update(String key, EvseForm evseForm) {
		LOG.info("Updating EVSE with key [{}]...", key);
		// Convert form to model
		EvseImpl evseImpl = evseMapper.toEvse(evseForm);
		// Update the EVSE
		Evse evse = evseStore.updateEVSE(key, evseImpl);
		// Create of update Connectors
		for (ConnectorForm connectorForm : evseForm.connectors()) {
			// Check if this connector exists already to decide whether it has to be updated
			// or created
			Optional<Connector> existingConnector = evse.getConnectors().stream()
					.filter(c -> c.getConnectorId().equals(connectorForm.connectorId())).findFirst();
			if (existingConnector.isPresent()) {
				connectorService.update(existingConnector.get().getKey(), connectorForm);
			} else {
				connectorService.create(evse.getKey(), connectorForm);
			}
		}
		// Delete connectors
		for (Connector connector : evse.getConnectors()) {
			if (evseForm.connectors().stream().noneMatch(c -> c.connectorId().equals(connector.getConnectorId()))) {
				connectorService.delete(connector.getKey());
			}
		}
		return evseStore.findByKey(key).get();
	}

	@Override
	public void patch(String key, EvseForm evseForm) {
		// Convert form to model
		EvseImpl evseImpl = evseMapper.toEvse(evseForm);
		// Update the EVSE
		evseStore.patchEVSE(key, evseImpl);
	}

}
